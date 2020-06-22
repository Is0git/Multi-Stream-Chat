package com.iso.chat.twitch_chat.api.twitch.models.channel

data class Channel(
	val total: Int? = null,
	val users: List<UsersItem>? = null
)