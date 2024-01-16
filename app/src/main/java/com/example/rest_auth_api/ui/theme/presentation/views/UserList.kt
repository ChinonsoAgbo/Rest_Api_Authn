package com.example.rest_auth_api.ui.theme.presentation.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rest_auth_api.model.data.UserData
import com.example.rest_auth_api.ui.theme.presentation.AppViewmodel
import com.example.rest_auth_api.ui.theme.presentation.dialogs.DeleteDialog

/**
 * Composable function representing the list view of users.
 *
 * This composable displays a list of users fetched from the ViewModel. It includes a clickable item for each user
 * with an option to delete the user, triggering a confirmation dialog.
 *
 * @see [AppViewmodel] for ViewModel handling user data.
 * @see [DeleteDialog] for the delete confirmation dialog.
 *
 * @since 1.0
 */
@Composable
fun UserListView() {
    // ViewModel instance for user data
    val viewModel = AppViewmodel.getInstance()
    // Collect the current list of users from the ViewModel
    val users by viewModel.users.collectAsState()

    // State variables for managing the delete dialog
    var openDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var userID by remember { mutableStateOf("") }

    // Trigger a user data fetch when the composable is launched

    LaunchedEffect(users) {
        viewModel.fetchUsers() // You can remove this line if the users are automatically updated

    }
    // Display a loading indicator if the users list is empty

    if (users.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()

        }
    } else {
        // Display a lazy column of clickable user items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(users) { user ->
                // Composable for a clickable user item
                ClickableUserItem(user, onUserDeleteClick = { userId ->
                    //viewModel.deleteUser(userId) // delete user
                    openDialog = true
                    userName = user.name
                    userID = userId
                    Log.e("UserList", "Delete user with ID: $userId")
                })
            }
        }
    }

    // Show delete dialog if openDialog is true
    DeleteDialog(
        openDialog = openDialog,
        userName = userName,
        action = {
            viewModel.deleteUser(userID)
        },
        userID = userID,
        onDismiss = { openDialog = false }
    )
}

/**
 * Composable function representing a clickable user item with an option to delete the user.
 *
 * @param user The [UserData] representing the user to be displayed.
 * @param onUserDeleteClick Callback function invoked when the delete icon is clicked.
 *
 * @since 1.0
 */
@Composable
fun ClickableUserItem(user: UserData, onUserDeleteClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle item click here */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Display user name
        Text(text = user.name)

        // Add delete icon
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            modifier = Modifier
                .size(24.dp)
                .clickable { onUserDeleteClick(user.id) }
        )
    }
}

