package com.uni.uniteaching.classes

import java.util.*

data class AttendanceEmbedded (
    val id : String="",
    val state:Boolean=false,
    val time: Date = Date()
)