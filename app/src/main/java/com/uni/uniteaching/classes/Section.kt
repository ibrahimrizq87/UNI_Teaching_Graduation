package com.uni.uniteaching.classes



data class Section (
    val sectionId : String="",
    val courseCode:String="",
    val courseName:String="",
    val lapID:String="",
    val assistantName:String="",
    val section : String="",
    val dep : String="",
    val day:String="",
    val time:String="",
    val endTime:String="",
    val isRunning:Boolean=false

)