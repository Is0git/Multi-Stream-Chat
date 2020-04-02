package com.android.multistreamchat.twitch_chat.api.twitch.models.channel

import com.squareup.moshi.Json

data class Channel(


	val total: Int? = null,


	val users: List<UsersItem>? = null
)