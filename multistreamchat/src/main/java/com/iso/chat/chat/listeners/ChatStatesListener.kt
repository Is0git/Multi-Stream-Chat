package com.iso.chat.chat.listeners

interface ChatStatesListener {
    fun onConnected()

    fun onError()

    fun onSend(line: String)
}