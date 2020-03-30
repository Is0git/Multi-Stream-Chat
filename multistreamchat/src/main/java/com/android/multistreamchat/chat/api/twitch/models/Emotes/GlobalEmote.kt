package com.android.multistreamchat.chat.api.twitch.models.Emotes

import com.android.multistreamchat.chat.chat_emotes.TwitchEmotesManager
import com.squareup.moshi.Json

data class GlobalEmote(

	@Json(name="emoticon_sets")
	val emoticon_sets: Map<String, List<TwitchEmotesManager.TwitchEmote>>? = null
)