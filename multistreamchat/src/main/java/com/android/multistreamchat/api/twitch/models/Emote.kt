package com.android.multistreamchat.api.twitch.models

import com.squareup.moshi.Json

data class Emote(

	@Json(name="code")
	val code: String? = null,

	val id: Int? = null
)