package com.example.rest_api_app.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class AuthResult (
    val AccessToken: String,
    val ExpiresIn: Int,
    val IdToken: String,
    val RefreshToken: String,
    val TokenType: String
)

data class AuthResponse(
    val AuthenticationResult: AuthResult,
    val ChallengeParameters: Any
){
    class Response: ResponseDeserializable<AuthResponse> {
        override fun deserialize(content:String): AuthResponse? =
             Gson().fromJson(content, AuthResponse::class.java)

}}