package com.android.multistreamchat.twitch_chat.api.twitch

import com.android.multistreamchat.twitch_chat.api.twitch.helper.getEmoteImageUrl
import com.android.multistreamchat.twitch_chat.api.twitch.models.Emotes.Emote
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.squareup.moshi.FromJson

class GlobalEmoteAdapter {

    @FromJson
    fun fromJsonEmotes(emote: Emote) : TwitchEmotesManager.TwitchEmote {
        return TwitchEmotesManager.TwitchEmote(emote.id!!, getEmoteImageUrl(emote.id), emote.code)
    }


}