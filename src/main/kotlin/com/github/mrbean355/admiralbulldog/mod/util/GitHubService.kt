package com.github.mrbean355.admiralbulldog.mod.util

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*

interface GitHubService {

    @GET("https://raw.githubusercontent.com/SteamDatabase/GameTracking-Dota2/master/game/dota/pak01_dir/resource/localization/{fileName}")
    @Streaming
    fun getStringsFile(@Path("fileName") fileName: String): Call<ResponseBody>

    @GET("https://api.github.com/repos/mrbean355/$GITHUB_REPO/releases/latest")
    fun getLatestRelease(): Call<LatestRelease>

    @POST("https://api.github.com/repos/MrBean355/$GITHUB_REPO/releases")
    fun createRelease(@Header("Authorization") auth: String, @Body request: ReleaseRequest): Call<ReleaseResponse>

    @PATCH("https://api.github.com/repos/MrBean355/$GITHUB_REPO/releases/{releaseId}")
    fun updateRelease(@Header("Authorization") auth: String, @Path("releaseId") releaseId: Long, @Body request: ReleaseRequest): Call<ReleaseResponse>

    @POST("https://uploads.github.com/repos/MrBean355/$GITHUB_REPO/releases/{releaseId}/assets")
    fun uploadReleaseAsset(@Header("Authorization") auth: String, @Path("releaseId") releaseId: Long, @Body body: RequestBody, @Query("name") name: String): Call<ResponseBody>

    companion object {
        val INSTANCE = Retrofit.Builder()
                .baseUrl("http://unused")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create<GitHubService>()
    }
}

data class LatestRelease(
        @SerializedName("tag_name")
        val tagName: String
)

data class ReleaseRequest(
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

data class ReleaseResponse(
        val id: Long
)
