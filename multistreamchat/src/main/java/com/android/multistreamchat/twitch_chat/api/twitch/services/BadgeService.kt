package com.android.multistreamchat.twitch_chat.api.twitch.services

import com.android.multistreamchat.twitch_chat.api.twitch.models.badges.BadgeItem
import com.android.multistreamchat.twitch_chat.api.twitch.models.channel.Channel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface BadgeService {
    @GET("kraken/chat/{id}/badges")
    @Headers("Client-Id: f0dmag7h9n8tj4710up57pjyooo46q")
    suspend fun getChannelBadges(@Path("id") id: Int,  @Query("api_version") apiVersion: Int = 5): Response<Map<String, BadgeItem>>
    @GET("kraken/users")
    @Headers("Client-Id: f0dmag7h9n8tj4710up57pjyooo46q")
    suspend fun getChannel(@Query("login") channelName: String, @Query("api_version") apiVersion: Int = 5): Response<Channel>
}