package com.uni.uniteaching.data

import com.uni.uniteaching.classes.Hall


interface FirebaseRealtimeRepo {
    suspend fun startGeneratingCode(hall: Hall, result :(Resource<String>) ->Unit)
    suspend fun getAttendWithCode(embeddedId:String,scannedCode:Int,result :(Resource<Boolean>) ->Unit)
}