package com.example.rest_auth_api.model.api

import com.example.rest_auth_api.model.data.UserData

interface Api {
    suspend fun  createUser(userName: String):String
    suspend fun deleteUser(id: String):String
    suspend fun fetchUser(): List<UserData>
    suspend fun isNetworkAvailable(): Boolean
}