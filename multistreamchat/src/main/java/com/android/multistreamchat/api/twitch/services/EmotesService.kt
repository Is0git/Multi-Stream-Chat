package com.android.multistreamchat.api.twitch.services

import com.android.multistreamchat.api.twitch.models.GlobalEmote
import retrofit2.Response
import retrofit2.http.*

interface EmotesService {

    @GET("/kraken/chat/emoticon_images")
    @Headers("Client-ID: kimne78kx3ncx6brgo4mv6wki5h1ko")
    suspend fun getGlobalEmotes(@Query("emotesets") vararg emoteSets: Int) : Response<GlobalEmote>

//    @GET
//    suspend fun getEmoteResource(@Url url: String, @Path(""))
}