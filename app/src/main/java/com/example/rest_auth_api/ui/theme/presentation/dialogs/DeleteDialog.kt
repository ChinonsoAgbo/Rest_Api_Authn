package com.example.rest_auth_api.ui.theme.presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Composable function representing a confirmation dialog for deleting a user.
 *
 * @param openDialog A boolean indicating whether the dialog should be open or closed.
 * @param userName The name of the user to be displayed in the dialog.
 * @param action Callback function invoked when the user confirms the deletion.
 * @param userID The ID of the user to be deleted.
 * @param onDismiss Callback function invoked when the dialog is dismissed.
 *
 * @since 1.0
 */
@Composable
fun DeleteDialog(
    openDialog: Boolean,
    userName: String,
    action: () -> Unit,
    userID: String,
    onDismiss: () -> Unit
) {

    if (openDialog) {

        AlertDialog(
            onDismissRequest = {
                // Invoke onDismiss when the user tries to dismiss the dialog

                onDismiss()
            },
            title = {
                // Display the title of the dialog

                Text(text = "Delete User")
            },
            text = {
                // Display the confirmation message with the user's name

                Text("Are sure you want to delete $userName ?")
            },
            confirmButton = {
                Button(
                    // Confirm button to execute the deletion action
                    onClick = {
                        action.invoke()
                        onDismiss()

                    }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                // Dismiss button to cancel the deletion action
                Button(

                    onClick = {
                        onDismiss()


                    }) {
                    Text("No")
                }
            },

            )
    }
}