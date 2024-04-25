package com.example.quizappbycouchbase

import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.MultipartBody
import okhttp3.ResponseBody

interface ApiService {
    @Multipart
    @POST("get_embedding")
    fun getImageEmbedding(@Part image: MultipartBody.Part): Call<EmbeddingResponse>
}

data class EmbeddingResponse(
    val embedding: List<Double>
)
