package com.android.multistreamchat.chat_emotes

import android.text.Spannable
import kotlinx.coroutines.Job

abstract class EmotesManager<K, T : EmotesManager.Emote>(var emoteStateListenerList: List<EmoteStateListener<K, T>>? = null) {

    var globalEmotes: MutableMap<K, List<T>> = mutableMapOf()

    var emoteDownloaderJob: Job? = null

    abstract fun getGlobalEmotes()

    fun clear() {
        emoteDownloaderJob?.run {
            cancel()
            null
        }
    }

    abstract fun createsSpannable(message: String, emotesId: Array<K>?)

    open class Emote(var code: String? = null)

    fun cancelJob() {
        emoteDownloaderJob?.cancel().also { emoteDownloaderJob = null }
    }

}