package com.iso.chat.twitch_chat.api.twitch.adapters

import com.iso.chat.twitch_chat.api.twitch.helpers.getEmoteImageUrl
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.squareup.moshi.FromJson

class SingleEmoteAdapter {
    @FromJson
    fun fromJsonSingleEmote(emote: com.iso.chat.twitch_chat.api.twitch.models.Emote.SingleEmote) : TwitchEmotesManager.TwitchEmote {
        return TwitchEmotesManager.TwitchEmote(emote.id!!, getEmoteImageUrl(emote.id), emote.code)
    }
}