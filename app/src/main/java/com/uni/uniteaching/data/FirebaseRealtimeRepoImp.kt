package com.uni.uniteaching.data

import com.google.firebase.database.DatabaseReference
import com.uni.uniteaching.classes.EmbeddedModel
import com.uni.uniteaching.classes.Hall
import javax.inject.Inject

class FirebaseRealtimeRepoImp @Inject constructor(
    private val database: DatabaseReference
        ): FirebaseRealtimeRepo {
    override  suspend fun getAttendWithCode(embeddedId:String,scannedCode:Int,result :(Resource<Boolean>) ->Unit) {
        database.child("embedded").child(embeddedId)
            .get()
            .addOnSuccessListener {
                val data = it.getValue(EmbeddedModel::class.java)
                if (data!!.code == scannedCode) {
                    result.invoke(Resource.Success(true))
                } else {
                    result.invoke(Resource.Success(false))
                }
            }
            .addOnFailureListener {
                result.invoke(Resource.Failure(it.localizedMessage))
            }
    }
        override  suspend fun startGeneratingCode(hall: Hall, result :(Resource<String>) ->Unit) {
            database.child("hall").child(hall.hallID)
                .setValue(hall)
                .addOnSuccessListener {
                    result.invoke(Resource.Success("success"))
                }
                .addOnFailureListener {
                    result.invoke(Resource.Failure(it.localizedMessage))
                }


        }
}