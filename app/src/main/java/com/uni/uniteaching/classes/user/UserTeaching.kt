package com.uni.uniteaching.classes.user


data class UserTeaching (
    var name: String="",
    var userId: String="",
    val code: String="",
    val nationalId: String="",
    val specialization :String = "",
    val userType:String="",
    var department:String=""
)
