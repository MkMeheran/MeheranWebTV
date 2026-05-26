package com.streamtv.data

import com.streamtv.model.Channel
import kotlinx.coroutines.delay

class ChannelRepository {
    
    val mockChannels = listOf(
        Channel(1,  "ATN Bangla",      "https://bozztv.com/rongo/rongo-AnandaTV/index.m3u8",  null, "entertainment"),
        Channel(2,  "Bangla Vision",   "https://bozztv.com/rongo/rongo-ATNNews/index.m3u8",  null, "entertainment"),
        Channel(3,  "BBC World News",  "https://bozztv.com/rongo/rongo-BTVChattagram/index.m3u8",  null, "news"),
        Channel(4,  "CNN International","https://bozztv.com/rongo/rongo-Channel24HD/index.m3u8", null, "news"),
        Channel(5,  "Al Jazeera",      "https://bozztv.com/rongo/rongo-DeshTV/index.m3u8",  null, "news"),
        Channel(6,  "Star Sports 1",   "https://tplay.live/out/bangladesh/ekhontv.index.m3u8",  null, "sports"),
        Channel(7,  "ESPN",            "http://tvn1.chowdhury-shaheb.com/gazitv/index.m3u8",  null, "sports"),
        Channel(8,  "GTV",             "https://bozztv.com/rongo/rongo-IndependentTV/index.m3u8",  null, "sports"),
        Channel(9,  "Cartoon Network", "https://bozztv.com/rongo/rongo-JamunaTelevision/index.m3u8",  null, "kids"),
        Channel(10, "Disney Channel",  "https://bozztv.com/rongo/rongo-somoy/index.m3u8",  null, "kids")
    )

    val mockCategories = listOf("All", "News", "Sports", "Entertainment", "Kids")

    val mockConfig = mapOf(
        "app_name" to "StreamTV",
        "overlay_timeout_ms" to "3500",
        "default_channel" to "1",
        "show_categories" to "true",
        "show_clock" to "true"
    )

    suspend fun getChannels(): List<Channel> {
        delay(500) // simulate network delay
        return mockChannels
    }
}
