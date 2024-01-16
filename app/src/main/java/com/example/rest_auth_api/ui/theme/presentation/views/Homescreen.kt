package com.example.rest_auth_api.ui.theme.presentation.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.rest_auth_api.R
import com.example.rest_auth_api.ui.theme.Rest_Auth_APITheme
import com.example.rest_auth_api.ui.theme.presentation.AppViewmodel
import com.example.rest_auth_api.ui.theme.presentation.dialogs.CustomDialog
/**
 * ViewModel instance used in this package.
 */
private val viewModel = AppViewmodel.getInstance()

/**
 * Composable function representing the home screen of the application.
 *
 * This composable function uses various Material3 components like Scaffold, TopAppBar, FloatingActionButton, etc.
 * It also utilizes a custom [CustomDialog] for user interaction.
 *
 * @see [UserListView] for displaying the list of users.
 *
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homescreen(){
    // State variables

    val showDialog = remember { mutableStateOf(false) }
    var userNameString = remember { mutableStateOf("") }
    Rest_Auth_APITheme {
        // Scaffold is a basic material design layout structure
            Scaffold(
                topBar = {
                    // customize the top bar here
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.menu)) },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = stringResource(
                                    R.string.user_s_page_des
                                )
                            )
                        }
                    )
                },
                floatingActionButton = {
                    // Floating Action Button for adding users
                    FloatingActionButton(
                        onClick = {
                            showDialog.value = true
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.Black
                        )
                    }
                },
            ) { paddingValue->// Content area
                // Surface is a basic container for other composables
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Display the list of users

                    UserListView()
                }
                    paddingValue
            }


        }
    // Display the custom dialog when showDialog is true
    if (showDialog.value) {
                CustomDialog(
                    value = "",
                    setShowDialog = {
                        showDialog.value = it // Set showDialog value based on user interaction
                    },
                ) { value ->
                    userNameString.value = value
                    // Handle user input and call ViewModel function to create a user
                    viewModel.createUser(userNameString.value)


                }
            }
    }
