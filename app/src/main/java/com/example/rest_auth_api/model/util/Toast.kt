package com.example.rest_auth_api.model.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
/**
 * Utility class for displaying Toast messages in the Android application.
 *
 * This class uses the Android `Toast` API to show messages on the UI thread.
 *
 * @see [ContextProvider] for obtaining the application context.
 *
 * @since 1.0
 */
class ToastMessage{
    // Application context obtained using ContextProvider
    private val appContext = ContextProvider.getInstance().getAppContext()

    /**
     * Displays a short duration Toast message indicating network unavailability.
     *
     * @param toastMessage The message to be displayed in the Toast.
     */
    fun showToast(toastMessage:String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(appContext, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

}