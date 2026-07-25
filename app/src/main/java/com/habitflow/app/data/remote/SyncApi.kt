package com.habitflow.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface SyncApi {
    @POST("api/sync")
    suspend fun sync(@Body body: SyncRequest): SyncResponse
}
