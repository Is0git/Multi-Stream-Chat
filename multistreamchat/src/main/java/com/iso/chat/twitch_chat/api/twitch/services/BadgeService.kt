package com.iso.chat.twitch_chat.api.twitch.services

import com.iso.chat.twitch_chat.api.RetrofitInstance.CLIENT_ID
import com.iso.chat.twitch_chat.api.twitch.models.badges.unofficial_badges.Badges
import com.iso.chat.twitch_chat.api.twitch.models.channel.Channel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface BadgeService {
    @GET
    suspend fun getChannelBadges(  @Url url: String? = null):  Response<Badges>

    @GET
    suspend fun getGlobalBadges(@Url url: String = "https://badges.twitch.tv/v1/badges/global/display"): Response<Badges>

    @GET
    @Headers("Client-ID: $CLIENT_ID", "Accept: application/vnd.twitchtv.v5+json")
    suspend fun getChannel( @Url url: String = "https://api.twitch.tv/kraken/users", @Query("login", encoded = true) name: String) : Response<Channel>
}