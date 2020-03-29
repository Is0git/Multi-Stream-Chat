package com.android.multistreamchat.chat_emotes

interface EmoteStateListener<K, T : EmotesManager.Emote> {
    fun onStartFetch()

    fun onEmotesFetched()

    fun onDownload()

    fun onFailed(throwable: Throwable?)

    fun onComplete(emoteSet: List<T>)
}