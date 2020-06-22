package com.iso.chat.twitch_chat.api.twitch.models.Emotes

import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.squareup.moshi.Json
import retrofit2.http.FieldMap

data class GlobalEmote(
	@FieldMap
	@Json(name="emoticon_sets")
	val emoticon_sets: Map<String, List<TwitchEmotesManager.TwitchEmote>>? = null
)