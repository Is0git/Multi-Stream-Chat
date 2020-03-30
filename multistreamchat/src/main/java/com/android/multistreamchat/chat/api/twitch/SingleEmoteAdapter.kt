package com.android.multistreamchat.chat.api.twitch

import com.android.multistreamchat.chat.api.twitch.helper.getEmoteImageUrl
import com.android.multistreamchat.chat.chat_emotes.TwitchEmotesManager
import com.squareup.moshi.FromJson

class SingleEmoteAdapter {
    @FromJson
    fun fromJsonSingleEmote(emote: com.android.multistreamchat.chat.api.twitch.models.Emote.SingleEmote) : TwitchEmotesManager.TwitchEmote {
        return TwitchEmotesManager.TwitchEmote(emote.id!!, getEmoteImageUrl(emote.id), emote.code)
    }
}