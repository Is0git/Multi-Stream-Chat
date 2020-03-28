package com.android.multistreamchat.chat_emotes

import android.text.Spannable
import kotlinx.coroutines.Job

abstract class EmotesManager<K, E : EmotesManager.Emote>(var emoteStateListenerList: List<EmoteStateListener<K, E>>? = null) {

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

    fun cancelJob() {
        emoteDownloaderJob?.cancel().also { emoteDownloaderJob = null }
    }

}