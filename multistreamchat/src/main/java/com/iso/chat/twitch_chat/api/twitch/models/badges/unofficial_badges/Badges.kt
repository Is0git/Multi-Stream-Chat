package com.iso.chat.twitch_chat.api.twitch.models.badges.unofficial_badges

import com.squareup.moshi.Json
import retrofit2.http.FieldMap

data class Badges(@field:Json(name ="badge_sets") @FieldMap var badgesSets: Map<String, Version>)