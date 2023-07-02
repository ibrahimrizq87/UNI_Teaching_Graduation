package com.uni.uniteaching.classes


import java.util.*


data class Attendance (
    var attendanceID : String="",
    val userName : String="",
    val userCode:String="",
    val time: Date = Date()
    )