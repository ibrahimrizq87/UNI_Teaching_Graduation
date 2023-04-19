package com.uni.uniteaching.data


import com.google.firebase.auth.FirebaseUser
import com.uni.uniteaching.classes.Permission
import com.uni.uniteaching.classes.user.UserStudent

interface AuthRepository {
    val user:FirebaseUser?
    suspend fun updateUserInfo(userStudent: UserStudent, result:(Resource<String>) ->Unit)
    suspend fun register(email:String, password:String, userStudent: UserStudent, result:(Resource<String>) -> Unit)
    suspend fun logOut(result:()->Unit)
    suspend fun addPermission(permission: Permission, result: (Resource<String>) -> Unit)
    fun storeSession(id :String, user : UserStudent, result :(UserStudent?)-> Unit)
    suspend fun getUserStudent(id :String,section:String,dep:String,grade:String, result:(Resource<UserStudent?>) -> Unit)
    fun getSessionStudent(result :(UserStudent?)-> Unit)
    fun setSession(user: UserStudent)


}