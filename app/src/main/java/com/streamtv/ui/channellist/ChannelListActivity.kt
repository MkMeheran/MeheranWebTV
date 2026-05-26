package com.streamtv.ui.channellist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.streamtv.data.ChannelRepository
import com.streamtv.model.Channel
import com.streamtv.ui.player.PlayerActivity
import com.streamtv.util.NumberInputHandler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChannelListActivity : AppCompatActivity() {
    private val repository = ChannelRepository()
    private lateinit var rvChannels: RecyclerView
    private lateinit var tvTime: TextView
    private lateinit var tvNumberInput: TextView
    private lateinit var btnReload: Button
    
    private var isGridView = true
    private var allChannels: List<Channel> = emptyList()
    private lateinit var numberInputHandler: NumberInputHandler
    private val timeHandler = Handler(Looper.getMainLooper())
    private val timeRunnable = object : Runnable {
        override fun run() {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            tvTime.text = sdf.format(Date())
            timeHandler.postDelayed(this, 60000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_channel_list", "layout", packageName))

        rvChannels = findViewById(resources.getIdentifier("rvChannels", "id", packageName))
        tvTime = findViewById(resources.getIdentifier("tvTime", "id", packageName))
        tvNumberInput = findViewById(resources.getIdentifier("tvNumberInput", "id", packageName))
        btnReload = findViewById(resources.getIdentifier("btnReload", "id", packageName))

        timeHandler.post(timeRunnable)

        numberInputHandler = NumberInputHandler(
            onChannelSelected = { channelNo ->
                val channel = allChannels.find { it.serial_no == channelNo }
                if (channel != null) {
                    launchPlayer(channel)
                }
            },
            onUpdateUI = { text ->
                if (text.isEmpty()) {
                    tvNumberInput.visibility = View.GONE
                } else {
                    tvNumberInput.text = text
                    tvNumberInput.visibility = View.VISIBLE
                }
            }
        )

        btnReload.setOnClickListener {
            loadChannels()
        }

        loadChannels()
    }

    private fun loadChannels() {
        lifecycleScope.launch {
            allChannels = repository.getChannels()
            updateLayoutManager()
        }
    }

    private fun updateLayoutManager() {
        if (isGridView) {
            rvChannels.layoutManager = GridLayoutManager(this, 3)
        } else {
            rvChannels.layoutManager = LinearLayoutManager(this)
        }
        rvChannels.adapter = ChannelAdapter(allChannels, isGridView) { launchPlayer(it) }
    }

    private fun toggleView() {
        isGridView = !isGridView
        updateLayoutManager()
    }

    private fun launchPlayer(channel: Channel) {
        val intent = Intent(this@ChannelListActivity, PlayerActivity::class.java)
        intent.putExtra("stream_url", channel.stream_url)
        intent.putExtra("channel_name", channel.name)
        intent.putExtra("channel_no", channel.serial_no)
        intent.putExtra("category", channel.category)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when {
            keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
                numberInputHandler.handleInput(keyCode - KeyEvent.KEYCODE_0)
                return true
            }
            keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_PROG_BLUE -> {
                toggleView()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeHandler.removeCallbacks(timeRunnable)
    }
}

class ChannelAdapter(
    private val channels: List<Channel>,
    private val isGrid: Boolean,
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvChannelNo: TextView = view.findViewById(view.resources.getIdentifier("tvChannelNo", "id", view.context.packageName))
        val tvChannelName: TextView = view.findViewById(view.resources.getIdentifier("tvChannelName", "id", view.context.packageName))
        val tvCategory: TextView = view.findViewById(view.resources.getIdentifier("tvCategory", "id", view.context.packageName))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (isGrid) "item_channel_grid" else "item_channel_list"
        val view = LayoutInflater.from(parent.context).inflate(
            parent.context.resources.getIdentifier(layoutId, "layout", parent.context.packageName), 
            parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = channels[position]
        holder.tvChannelNo.text = if (isGrid) "CH ${channel.serial_no}" else channel.serial_no.toString()
        holder.tvChannelName.text = channel.name
        holder.tvCategory.text = channel.category?.uppercase()
        holder.itemView.setOnClickListener { onClick(channel) }
    }

    override fun getItemCount() = channels.size
}
