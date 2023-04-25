package com.uni.uniteaching.classes.courses

data class AssistantCourses (
    override val courseCode:String="",
    override val grade : String="",
    override val professor: String="",
    var sections:List<String>

):Course