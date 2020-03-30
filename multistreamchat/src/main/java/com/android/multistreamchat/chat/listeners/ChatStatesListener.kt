package com.android.multistreamchat.chat.listeners

interface ChatStatesListener {
    fun onConnected()

    fun onError()

    fun onSend(line: String)
}