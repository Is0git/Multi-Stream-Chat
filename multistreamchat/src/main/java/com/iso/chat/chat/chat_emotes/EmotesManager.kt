package com.iso.chat.chat.chat_emotes

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import com.iso.chat.chat.listeners.EmoteStateListener
import kotlinx.coroutines.Job

abstract class EmotesManager<K, T : EmotesManager.Emote>(var emoteStateListenerList: List<EmoteStateListener<K, T>>? = null) {

    var globalEmotes: MutableMap<K, List<T>> = mutableMapOf()
    var emotesDownloadJob: Job? = null
    var singleEmoteDownloadJob: Job? = null
    val cachedEmotes: MutableMap<String, Bitmap> by lazy { mutableMapOf<String, Bitmap>()}

    abstract fun getEmotesPositionPairs(emotesRaw: String?) : Map<String, List<Pair<Int, Int>>>
    abstract fun getGlobalEmotes()
    abstract suspend fun getEmoteBitmap(emote: T) : Bitmap
    abstract suspend fun getEmote(id: Int) : T
    abstract suspend fun appendEmotes(pairs: Map<String, List<Pair<Int, Int>>>?, spannable: SpannableStringBuilder) : Spannable?
    abstract suspend fun createEmoteSpannable(message: String, emotes: Array<T?>?, spannable: SpannableStringBuilder) : Spannable

    open class Emote(var code: String? = null,  var imageUrl: String? = null) {
        @Transient
        var emoteBitMap: Bitmap? = null
        @Transient
        var emoteDrawable: Drawable? = null
    }

    fun clear() {
        emotesDownloadJob?.cancel().also { emotesDownloadJob = null }
        singleEmoteDownloadJob?.cancel().also { singleEmoteDownloadJob = null }
        emoteStateListenerList = null
    }

}