package com.streamtv.model

data class Channel(
    val serial_no: Int,
    val name: String,
    val stream_url: String,
    val logo_url: String?,
    val category: String?
)
