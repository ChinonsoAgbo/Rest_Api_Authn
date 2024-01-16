package com.example.rest_auth_api.model.util

import android.content.Context
/**
 * Singleton utility class for providing access to the application context.
 *
 * The `ContextProvider` is responsible for storing and providing the application context.
 *
 * @property appContext The application context obtained during initialization.
 *
 * @since 1.0
 */
class ContextProvider private constructor(private val appContext:Context ){
    /**
     * Singleton companion object to manage the instance of `ContextProvider`.
     */
    companion object{
        private lateinit var instance: ContextProvider
        /**
         * Initializes the `ContextProvider` with the application context.
         *
         * @param context The application context to be stored.
         */
        fun initialize(context: Context){
            instance = ContextProvider(context.applicationContext)

        }
        /**
         * Gets the singleton instance of `ContextProvider`.
         *
         * @return The instance of `ContextProvider`.
         */
        fun getInstance(): ContextProvider {
            return instance
        }
    }

    /**
     * Gets the application context stored in the `ContextProvider`.
     *
     * @return The application context.
     */
    fun getAppContext(): Context {
        return appContext
    }

}
