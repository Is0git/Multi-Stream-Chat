package com.iso.chat.twitch_chat.api.twitch.models.Emote

import com.squareup.moshi.Json

data class SingleEmote(

	@Json(name="emoticon_set")
	val emoticonSet: Int? = null,

	@Json(name="channel_name")
	val channelName: Any? = null,

	@Json(name="code")
	val code: String? = null,

	@Json(name="id")
	val id: Int? = null,

	@Json(name="channel_id")
	val channelId: Any? = null
)