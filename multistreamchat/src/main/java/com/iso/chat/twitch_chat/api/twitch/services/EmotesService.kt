package com.iso.chat.twitch_chat.api.twitch.services

import com.iso.chat.twitch_chat.api.twitch.models.Emotes.GlobalEmote
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface EmotesService {

    @GET("/kraken/chat/emoticon_images")
    @Headers("Client-ID: f0dmag7h9n8tj4710up57pjyooo46q", "Accept: application/vnd.twitchtv.v5+json")
    suspend fun getGlobalEmotes(@Query("emotesets") vararg emoteSets: Int) : Response<GlobalEmote>

    @GET
    suspend fun getEmote(@Url url: String = "https://api.twitchemotes.com/api/v4/emotes", @Query("id") id: Int) : Response<List<TwitchEmotesManager.TwitchEmote>>

//    @GET
//    suspend fun getEmoteResource(@Url url: String, @Path(""))
}