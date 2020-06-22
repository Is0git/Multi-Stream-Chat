package com.iso.chat.twitch_chat.api.twitch.models.badges.unofficial_badges

import android.graphics.Bitmap
import com.squareup.moshi.Json

data class BadgeItem(

	@field:Json(name="last_updated")
	val lastUpdated: Any? = null,

	@field:Json(name="image_url_4x")
	val imageUrl4x: String? = null,

	@field:Json(name="description")
	val description: String? = null,

	@field:Json(name="image_url_2x")
	val imageUrl2x: String? = null,

	@field:Json(name="image_url_1x")
	val imageUrl1x: String? = null,

	@field:Json(name="title")
	val title: String? = null,

	@field:Json(name="click_action")
	val clickAction: String? = null,

	@field:Json(name="click_url")
	val clickUrl: String? = null,

	@Transient
	var badgeBitmap: Bitmap? = null
)