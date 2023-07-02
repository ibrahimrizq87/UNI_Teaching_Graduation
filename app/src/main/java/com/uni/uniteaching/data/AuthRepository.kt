package com.uni.uniteaching.data


import com.google.firebase.auth.FirebaseUser
import com.uni.uniteaching.classes.Permission
import com.uni.uniteaching.classes.user.UserTeaching

interface AuthRepository {
    val user:FirebaseUser?
    suspend fun logIn(email:String, password:String, result:(Resource<String>) ->Unit)

    suspend fun updateUserInfo(userStudent: UserTeaching, result:(Resource<String>) ->Unit)
    suspend fun register(email:String, password:String, userStudent: UserTeaching, result:(Resource<String>) -> Unit)
    suspend fun logOut(result:()->Unit)
    fun storeSession(id :String, user : UserTeaching, result :(UserTeaching?)-> Unit)
    suspend fun getUser(id :String, result:(Resource<UserTeaching?>) -> Unit)
    fun getSessionStudent(result :(UserTeaching?)-> Unit)
    fun setSession(user: UserTeaching)


}