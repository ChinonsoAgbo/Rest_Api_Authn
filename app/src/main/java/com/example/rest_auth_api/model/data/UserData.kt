package com.example.rest_auth_api.model.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class UserData(

    val name:String,
    val id: String
) {

    class PostResponse:  ResponseDeserializable<Array<UserData>> {
        override fun deserialize(content:String): Array<UserData>? {
            return Gson().fromJson(content, Array<UserData>::class.java)
        }
    }
}