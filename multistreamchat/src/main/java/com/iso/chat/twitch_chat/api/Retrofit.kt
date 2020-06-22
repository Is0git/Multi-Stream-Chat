package com.iso.chat.twitch_chat.api

import com.iso.chat.twitch_chat.api.twitch.adapters.GlobalEmoteAdapter
import com.iso.chat.twitch_chat.api.twitch.adapters.SingleEmoteAdapter
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    const val CLIENT_ID = "f0dmag7h9n8tj4710up57pjyooo46q"
    fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).build())
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                .add(GlobalEmoteAdapter())
                .add(SingleEmoteAdapter())
                .build()))
            .build()
    }
}