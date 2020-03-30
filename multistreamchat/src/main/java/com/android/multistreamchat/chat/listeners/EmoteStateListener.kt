package com.android.multistreamchat.chat.listeners

import com.android.multistreamchat.chat.chat_emotes.EmotesManager

interface EmoteStateListener<K, T : EmotesManager.Emote> {
    fun onStartFetch()

    fun onEmotesFetched()

    fun onDownload()

    fun onFailed(throwable: Throwable?)

    fun onComplete(emoteSet: List<T>)
}