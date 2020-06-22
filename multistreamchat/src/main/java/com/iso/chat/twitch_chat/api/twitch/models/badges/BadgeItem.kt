package com.iso.chat.twitch_chat.api.twitch.models.badges

import android.graphics.Bitmap

data class BadgeItem(val alpha: String?, val image: String?, val svg: String?, @Transient var imageBitmap: Bitmap? = null)