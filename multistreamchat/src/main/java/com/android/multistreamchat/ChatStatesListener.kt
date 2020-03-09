package com.android.multistreamchat

interface ChatStatesListener {
    fun onConnected()

    fun onError()

    fun onSend(line: String)
}