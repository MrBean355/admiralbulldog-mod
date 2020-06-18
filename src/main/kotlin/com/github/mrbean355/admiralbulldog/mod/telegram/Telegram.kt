package com.github.mrbean355.admiralbulldog.mod.telegram

import com.github.mrbean355.admiralbulldog.mod.telegram.Telegram.Service.Companion.INSTANCE
import com.github.mrbean355.admiralbulldog.mod.telegram.Telegram.Service.Companion.TOKEN
import okhttp3.ResponseBody
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

object Telegram {
    private const val CHAT_ID = "@bulldog_sounds"

    fun sendChannelMessage(message: String) {
        if (TOKEN != null) {
            INSTANCE.sendMessage(CHAT_ID, "html", message).execute()
        } else {
            LoggerFactory.getLogger(Telegram::class.java).info("$CHAT_ID: $message")
        }
    }

    private interface Service {

        @GET("sendMessage")
        fun sendMessage(@Query("chat_id") chatId: String, @Query("parse_mode") parseMode: String, @Query("text") text: String): Call<ResponseBody>

        companion object {
            val TOKEN: String? = System.getenv("TELEGRAM_TOKEN")
            val INSTANCE: Service = Retrofit.Builder()
                    .baseUrl("https://api.telegram.org/bot$TOKEN/")
                    .build()
                    .create()
        }
    }
}