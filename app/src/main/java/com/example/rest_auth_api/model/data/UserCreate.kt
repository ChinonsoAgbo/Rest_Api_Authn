package com.example.rest_auth_api.model.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
/**
 * Data class representing a user creation request.
 *
 * @property name The name of the user.
 */
data class UserCreate(val name:String){
    /**
     * ResponseDeserializable implementation for deserializing UserCreate objects.
     */
    class PostUserData:ResponseDeserializable<UserCreate>{
        /**
         * Deserialize the JSON content into a UserCreate object.
         *
         * @param content The JSON content to deserialize.
         * @return A UserCreate object or null if deserialization fails.
         */
        override fun deserialize(content: String): UserCreate? {
            return try {
                Gson().fromJson(content,UserCreate::class.java)
            }catch(e: IllegalStateException){
                e.printStackTrace()

                null
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}

