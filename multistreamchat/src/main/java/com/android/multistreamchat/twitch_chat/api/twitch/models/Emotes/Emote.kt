package com.android.multistreamchat.twitch_chat.api.twitch.models.Emotes

import com.squareup.moshi.Json

data class Emote(

	@Json(name="code")
	val code: String? = null,

	val id: Int? = null
)