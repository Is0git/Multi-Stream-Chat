package com.android.multistreamchat.api.twitch.models

import com.android.multistreamchat.chat_emotes.TwitchEmotesManager
import com.squareup.moshi.Json

data class GlobalEmote(

	@Json(name="emoticon_sets")
	val emoticonSets: Map<String, List<TwitchEmotesManager.TwitchEmote>>? = null
)