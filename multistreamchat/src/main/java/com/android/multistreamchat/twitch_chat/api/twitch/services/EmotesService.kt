package com.android.multistreamchat.twitch_chat.api.twitch.services

import com.android.multistreamchat.twitch_chat.api.twitch.models.Emotes.GlobalEmote
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface EmotesService {

    @GET("/kraken/chat/emoticon_images")
    @Headers("Client-ID: kimne78kx3ncx6brgo4mv6wki5h1ko")
    suspend fun getGlobalEmotes(@Query("emotesets") vararg emoteSets: Int) : Response<GlobalEmote>

    @GET
    suspend fun getEmote(@Url url: String = "https://api.twitchemotes.com/api/v4/emotes", @Query("id") id: Int) : Response<List<TwitchEmotesManager.TwitchEmote>>

//    @GET
//    suspend fun getEmoteResource(@Url url: String, @Path(""))
}