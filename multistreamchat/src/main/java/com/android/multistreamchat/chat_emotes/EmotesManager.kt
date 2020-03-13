package com.android.multistreamchat.chat_emotes

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

    open class Emote(var code: String? = null)

}