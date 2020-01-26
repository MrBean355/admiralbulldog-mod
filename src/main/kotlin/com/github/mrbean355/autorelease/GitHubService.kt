package com.github.mrbean355.autorelease

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*

interface GitHubService {

    @GET("https://api.github.com/repos/mrbean355/admiralbulldog-mod/releases/latest")
    fun getLatestRelease(): Call<LatestRelease>

    @POST("https://api.github.com/repos/MrBean355/admiralbulldog-mod/releases")
    fun createRelease(@Header("Authorization") auth: String, @Body request: CreateReleaseRequest): Call<CreateReleaseResponse>

    @POST("https://uploads.github.com/repos/MrBean355/admiralbulldog-mod/releases/{releaseId}/assets")
    fun uploadReleaseAsset(@Header("Authorization") auth: String, @Path("releaseId") releaseId: Long, @Body body: RequestBody, @Query("name") name: String): Call<ResponseBody>
}

data class LatestRelease(
        @SerializedName("tag_name")
        val tagName: String
)

data class CreateReleaseRequest(
        @SerializedName("tag_name")
        val tagName: String,
        @SerializedName("target_commitish")
        val targetCommitish: String,
        val name: String,
        val body: String,
        val draft: Boolean,
        @SerializedName("prerelease")
        val preRelease: Boolean
)

data class CreateReleaseResponse(
        val id: Long
)

val service by lazy {
    Retrofit.Builder()
            .baseUrl("https://unused/")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create<GitHubService>()
}
