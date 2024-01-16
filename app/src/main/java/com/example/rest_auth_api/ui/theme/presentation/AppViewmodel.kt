package com.example.rest_auth_api.ui.theme.presentation


import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rest_auth_api.model.data.UserData
import com.example.rest_auth_api.model.api.ApiService
import com.example.rest_auth_api.model.util.NetworkStateReceiver
import com.example.rest_auth_api.model.util.ToastMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * ViewModel class responsible for managing the data and business logic related to user operations in the application.
 *
 * This ViewModel communicates with the [ApiService] to perform CRUD operations on user data.
 * It also observes network connectivity using [NetworkStateReceiver] and provides LiveData updates for network state changes.
 *
 * @see [ApiService] for handling API requests and responses.
 * @see [NetworkStateReceiver] for monitoring network connectivity changes.
 * @see [ToastMessage] for displaying toast messages.
 *
 * @property api Instance of [ApiService] used for making API calls.
 * @property _users MutableStateFlow representing the current list of users.
 * @property users Immutable StateFlow exposing the current list of users.
 * @property _isNetworkAvailable MutableLiveData representing the current network connectivity state.
 * @property isNetworkAvailable LiveData exposing the network connectivity state.
 * @property toast Instance of [ToastMessage] for displaying toast messages.
 *
 * @constructor Creates an instance of [AppViewmodel].
 */
class AppViewmodel():ViewModel() {
    //private val apiService: ApiService
    private val api = ApiService()
    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val users: StateFlow<List<UserData>> = _users.asStateFlow()


    private val toast=ToastMessage()

    /**
     * Companion object to provide a singleton instance of [AppViewmodel].
     */

    companion object {
        private var instance: AppViewmodel? = null
        /**
         * Gets the singleton instance of [AppViewmodel]. If the instance is null, it creates a new one.
         */
        fun getInstance(): AppViewmodel {
            if (instance == null) {
                instance = AppViewmodel( )
            }
            return instance as AppViewmodel
        }
    }

    /**
     * Coroutine function to fetch the list of users from the API and update the [_users] StateFlow.
     *
     * In case of an exception, it logs the error and displays a toast message indicating the network unavailability.
     */
    suspend fun fetchUsers() {

        try {
            val fetchedUsers = api.fetchUser()

            _users.value = fetchedUsers
        } catch (e: Exception) {
            // Handle error or log it
            Log.e("Error", e.message.toString())
            toast.showToast(toastMessage = "No users fetched")
        }

    }
    /**
     * Coroutine function to delete a user by ID. It updates the [_users] StateFlow after deletion.
     *
     * In case of an exception, it logs the error and displays a toast message indicating the failure to delete the user.
     *
     * @param id The ID of the user to be deleted.
     */
    fun deleteUser(id: String) {
        viewModelScope.launch {
            try {
                _users.value = emptyList()

                val response = api.deleteUser(id)
                Log.e("continuationLog", response)

                // make a toast based on the [ApiService] response
                val toastMessage = when (response) {
                    "204", "200" -> "User deleted successfully"
                    "401" -> "Cannot delete this user"
                    "500" -> "Server not reachable"
                    "Network not available" -> "Network not available"
                    else -> "Delete not successful"
                }

                toast.showToast(toastMessage) // toast

                // delay b4 fetching new user
                delay(2000)
                fetchUsers()

            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                toast.showToast(toastMessage = "User cannot be deleted")

            }
        }
    }
    /**
     * Coroutine function to create a new user with the provided username. It updates the [_users] StateFlow after creation.
     *
     * In case of an exception, it logs the error and displays a toast message indicating the failure to create the user.
     *
     * @param userName The username of the new user to be created.
     */
    fun createUser(userName: String) {
        viewModelScope.launch {
            try {
                _users.value = emptyList()

                // make a toast based on the [ApiService] response
                val toastMessage =
                    when (api.createUser(userName))// create new user
                    {
                        "201" -> "User created successfully!"
                        "401" -> "Cannot create this user" //Unauthorized Request
                        "500" -> "Server not reachable"// ser issues
                        "Network not available" -> "Network not available"
                        else -> "Create user not successful"
                    }

                toast.showToast(toastMessage) // toast

                delay(2000)

                fetchUsers()

            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                toast.showToast(toastMessage = "User cannot be created ")

            }
        }
    }
}