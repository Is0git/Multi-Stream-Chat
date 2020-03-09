package com.android.multistreamchat

interface DataListener {
    fun onReceive(message: ChatParser.Message)
}