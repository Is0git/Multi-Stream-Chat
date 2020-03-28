package com.android.multistreamchat.api.twitch.models

import com.squareup.moshi.Json

data class GlobalEmote(

	@Json(name="emoticon_sets")
	val emoticonSets: EmoticonSets? = null
)