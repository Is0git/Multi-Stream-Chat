package com.android.multistreamchat.chat_emotes

import android.text.Spannable
import android.text.SpannableString
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class EmotesManager<K, E : EmotesManager.Emote> {

    var globalEmotes: MutableMap<K, E> = mutableMapOf()

    var emoteDownloaderJob: Job? = null

    abstract fun getGlobalEmotes()

    fun clear() {
        emoteDownloaderJob?.run {
            cancel()
            null
        }
    }


    abstract fun createsSpannable(message: String, emotesId: Array<K>?) : Spannable?

    open class Emote(var code: String? = null)

}