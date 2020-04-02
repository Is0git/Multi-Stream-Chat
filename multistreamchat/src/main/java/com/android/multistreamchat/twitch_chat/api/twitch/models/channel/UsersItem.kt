package com.android.multistreamchat.twitch_chat.api.twitch.models.channel

import com.squareup.moshi.Json

data class UsersItem(

	@Json(name="updated_at")
	val updatedAt: String? = null,

	val name: String? = null,

	val bio: Any? = null,

	@Json(name="created_at")
	val createdAt: String? = null,

	@Json(name="logo")
	val logo: String? = null,

	val _id: String? = null,

	@Json(name="display_name")
	val displayName: String? = null,

	@Json(name="type")
	val type: String? = null
)