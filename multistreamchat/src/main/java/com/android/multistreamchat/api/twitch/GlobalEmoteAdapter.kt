package com.android.multistreamchat.api.twitch

import com.android.multistreamchat.api.twitch.models.Emote
import com.android.multistreamchat.chat_emotes.TwitchEmotesManager
import com.bumptech.glide.Glide
import com.squareup.moshi.FromJson

class GlobalEmoteAdapter {

    @FromJson
    fun fromJson(emote: Emote) : TwitchEmotesManager.TwitchEmote {
        val url = "https://static-cdn.jtvnw.net/emoticons/v1/${emote.id}/1.0"
        return TwitchEmotesManager.TwitchEmote(emote.id!!, url, emote.code)
    }
}