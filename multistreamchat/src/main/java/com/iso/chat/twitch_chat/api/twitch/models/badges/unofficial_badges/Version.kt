package com.iso.chat.twitch_chat.api.twitch.models.badges.unofficial_badges

import com.squareup.moshi.Json
import retrofit2.http.FieldMap

data class Version(@field:Json(name = "versions") @FieldMap var versions: MutableMap<String, BadgeItem>)