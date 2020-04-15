package com.android.multistreamchat.chat.chat_emotes

import android.graphics.drawable.Drawable
import android.text.Spannable
import com.android.multistreamchat.chat.listeners.EmoteStateListener
import kotlinx.coroutines.Job

abstract class EmotesManager<K, T : EmotesManager.Emote>(var emoteStateListenerList: List<EmoteStateListener<K, T>>? = null) {

    var globalEmotes: MutableMap<K, List<T>> = mutableMapOf()

    var emotesDownloadJob: Job? = null

    var singleEmoteDownloadJob: Job? = null

    abstract fun extractEmoteIds(emotesRaw: String?) : Array<String>?

    abstract suspend fun getMessageEmoteCodes(emotesIds: Array<String>) : Array<T?>

    abstract fun getGlobalEmotes()

    abstract suspend fun getEmoteDrawable(emote: T) : Drawable

    abstract suspend fun getEmote(id: Int) : T

    abstract suspend fun createEmoteSpannable(message: String, emotes: Array<T?>) : Spannable

    open class Emote(var code: String? = null,  var imageUrl: String? = null)

    fun clear() {
        emotesDownloadJob?.cancel().also { emotesDownloadJob = null }
        singleEmoteDownloadJob?.cancel().also { singleEmoteDownloadJob = null }
        emoteStateListenerList = null
    }

}