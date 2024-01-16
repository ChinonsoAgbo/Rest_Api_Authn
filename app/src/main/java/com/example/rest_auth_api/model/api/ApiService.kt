package com.example.rest_auth_api.model.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rest_api_app.model.AuthResponse
import com.example.rest_auth_api.model.data.UserCreate
import com.example.rest_auth_api.model.data.UserData
import com.example.rest_auth_api.model.util.ContextProvider
import com.example.rest_auth_api.model.util.ToastMessage
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import com.github.kittinunf.result.Result
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation of the [Api] interface for making API requests related to user data.
 *
 * This class uses the Fuel HTTP library for making HTTP requests and handles authentication with OAuth2.
 *
 * @property clientId The client ID used for OAuth2 authentication.
 * @property password The client secret used for OAuth2 authentication.
 * @property username The username used for OAuth2 authentication.
 * @property apiGetTokenUrl The URL for obtaining the authentication token.
 * @property postUr The base URL for posting user data.
 * @property getUsersURL The URL for fetching user data.
 * @property ids List to store user IDs retrieved from the API.
 * @property appContext Application context obtained using [ContextProvider].
 * @property tokenDeferred A [CompletableDeferred] instance used to ensure the authentication token is fetched before making API requests.
 * @property token Authentication token used for API requests.
 * @property toast Instance of [ToastMessage] for displaying toast messages.
 *
 * @constructor Creates an instance of [ApiService] and initiates the authentication token retrieval in the background.
 */
@OptIn(DelicateCoroutinesApi::class)
class ApiService : Api {

    private val clientId = "2c4odl7eielf635hgfvmeuqi01" // add your client id
    private val password = "AppLab173281" // add your client secret
    private val username = "cagbo"
    private val apiGetTokenUrl =
        "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_M0GpoxnFg/oauth2/token" // url to get the token
    private val postUr =
        "https://9ty6vxsuu3.execute-api.eu-central-1.amazonaws.com/users" // url to get the token

    private val getUsersURL: String =
        "https://9ty6vxsuu3.execute-api.eu-central-1.amazonaws.com/users"
    private var ids: MutableList<String> = mutableListOf()

    private val appContext = ContextProvider.getInstance().getAppContext()
    private val tokenDeferred = CompletableDeferred<Unit>()
    private var token by mutableStateOf("")
    private val toast = ToastMessage()

    /**
     * Initializes the ApiService and initiates the token retrieval in the background.
     */

    init {
        GlobalScope.launch {
            setAuthToken()
        }
    }

    /**
     *  method to fetch and set the authentication token.
     * Throws [NetworkUnavailableException] if the network is not available.
     */
    private suspend fun setAuthToken() {
        if (!isNetworkAvailable()) {
            // Handle the case when the network is not available
            toast.showToast(toastMessage = "Network is not available")
        } else {

            val bodyJson = """
            {
                "AuthParameters": {
                    "USERNAME": "$username",
                    "PASSWORD": "$password"
                },
                "AuthFlow": "USER_PASSWORD_AUTH",
                "ClientId": "$clientId"
            }
        """.trimIndent()

            try {
                apiGetTokenUrl.httpPost()
                    .jsonBody(bodyJson)
                    .header("Content-Type" to "application/x-amz-json-1.1")
                    .header("X-Amz-Target" to "AWSCognitoIdentityProviderService.InitiateAuth")
                    .responseObject(AuthResponse.Response()) { _, response, result ->
                        when (result) {
                            is Result.Success -> {
                                token = result.value.AuthenticationResult.AccessToken
                                tokenDeferred.complete(Unit)
                                Log.e("tokenValues", "Grandted: $token")

                            }

                            is Result.Failure -> {
                                // handle error
                                Log.e("tokenValuesE", "Error: ${result.error}")
                                tokenDeferred.completeExceptionally(result.error.exception)
                            }

                            else -> {}
                        }
                    }
            } catch (e: IllegalStateException) {
                e.printStackTrace()

            } catch (e: Exception) {
                e.printStackTrace()
                tokenDeferred.completeExceptionally(e)
            }
        }
    }

    /**
     * Coroutine method to ensure the authentication token is fetched before proceeding with API requests.
     */
    private suspend fun ensureTokenFetched() {
        // Wait for the token to be set before proceeding
        tokenDeferred.await()
    }

    /**
     * Fetches user data from the API and returns a list of [UserData].
     * Throws [NetworkUnavailableException] if the network is not available.
     */
    override suspend fun fetchUser(): List<UserData> {
        ensureTokenFetched()

        return if (!isNetworkAvailable()) {
            // Handle the case when the network is not available
            toast.showToast(toastMessage = "Network is not available")
            emptyList()
        } else {
            suspendCoroutine { continuation ->
                try {
                    getUsersURL.httpGet()
                        .authentication()
                        .bearer(token)
                        .responseObject(UserData.PostResponse()) { _, _, result ->
                            when (result) {
                                is Result.Success -> {
                                    val (users, _) = result
                                    users?.let { userdata ->
                                        continuation.resume(userdata.toList())
                                        Log.e("UsersAPI", userdata.toList().toString())
                                        Log.d("FetchUser", "Users loaded: ${userdata.toList()}")

                                    }
                                }

                                is Result.Failure -> {
                                    continuation.resumeWithException(result.getException())
                                    Log.e("UsersAPIE", result.getException().toString())
                                }
                            }
                        }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Deletes a user with the specified ID from the API.
     * Throws [NetworkUnavailableException] if the network is not available.
     */
    override suspend fun deleteUser(id: String): String {
        if (!isNetworkAvailable()) {
            // Handle the case when the network is not available
            toast.showToast(toastMessage = "Network is not available")
            return "Network not available"
        } else {
            ensureTokenFetched()

            return try {
                suspendCoroutine { continuation ->
                    "$postUr/$id".httpDelete()
                        .authentication()
                        .bearer(token)
                        .response { _, response, result ->
                            result.fold(
                                success = {
                                    Log.e(
                                        "DeleteUserResponse",
                                        "HTTP ${response.statusCode}: ${response.responseMessage}"
                                    )
                                    continuation.resume(response.statusCode.toString())

                                },
                                failure = { error ->
                                    Log.e("DeleteUserResponse", "Error: ${error.message}")
                                    continuation.resume("${error.response.statusCode}")
                                }
                            )
                        }
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                return "IllegalStateException"
            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"
            }
        }
    }

    /**
     * Creates a new user with the specified username.
     * Throws [NetworkUnavailableException] if the network is not available.
     */
    override suspend fun createUser(userName: String):String {
        if (!isNetworkAvailable()) {
            // Handle the case when the network is not available
            toast.showToast(toastMessage = "Network is not available")
            return "Network not available"

        } else {

            ensureTokenFetched()
            val bodyJson = """
                {
                    "name": "$userName"
                }
            """.trimIndent()

            return try {
                suspendCoroutine { continuation ->
                    postUr.httpPost()
                        .body(bodyJson)
                        .authentication()
                        .bearer(token)
                        .responseObject(UserCreate.PostUserData()) { _, response, result ->

                            result.fold(
                                success = { data ->
                                    // Handle successful response
                                    Log.e("PostResponse", response.responseMessage)
                                    Log.e("PostResponse", "$data")
                                    Log.e("PostResponseCode", "${response.statusCode}")

                                    Log.e("token!", token)
                                    continuation.resume(response.statusCode.toString())

                                },
                                failure = { error ->
                                    //  error
                                    Log.e("PostResponse", response.responseMessage)
                                    Log.e("PostResponseCode", "${response.statusCode}")
                                    Log.e("PostResponse", "${error.response}")

                                    Log.e("token!", token)
                                    continuation.resume("${error.response.statusCode}")

                                }
                            )

                        }
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                return "IllegalStateException"

            } catch (e: Exception) {
                e.printStackTrace()
                return "Exception"

            }
        }
    }

    /**
     * Checks if the network is available.
     * Returns `true` if the network is available, `false` otherwise.
     */

    override suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                return@withContext capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo
                return@withContext networkInfo != null && networkInfo.isConnected
            }
        } catch (e: NetworkUnavailableException) {

            Log.e("NetworkAvailability", "Error checking network availability", e)
            toast.showToast(toastMessage = "Network is not available")

            return@withContext false

        }
    }


}
