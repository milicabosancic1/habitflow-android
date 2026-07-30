package com.habitflow.app.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.habitflow.app.BuildConfig

data class GoogleSignInResult(val idToken: String, val email: String, val displayName: String)

/**
 * Tanak omotač oko Credential Manager-a. Poziva se iz Composable-a
 * (Credential Manager-ov bottom sheet zahteva Activity kontekst).
 */
class GoogleAuthClient {
    suspend fun signIn(context: Context): GoogleSignInResult {
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val response = CredentialManager.create(context).getCredential(context, request)
        val credential = GoogleIdTokenCredential.createFrom(response.credential.data)
        return GoogleSignInResult(
            idToken = credential.idToken,
            email = credential.id,
            displayName = credential.displayName ?: credential.id
        )
    }
}
