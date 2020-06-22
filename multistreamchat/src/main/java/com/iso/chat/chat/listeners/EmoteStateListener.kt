package com.iso.chat.chat.listeners

import com.iso.chat.chat.chat_emotes.EmotesManager

interface EmoteStateListener<K, T : EmotesManager.Emote> {
    fun onStartFetch()

    fun onEmotesFetched(emoteSet: List<T>)

    fun onDownload()

    fun onFailed(throwable: Throwable?)

    fun onComplete()
}