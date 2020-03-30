package com.android.multistreamchat.chat.listeners

import com.android.multistreamchat.chat.chat_parser.ChatParser

interface DataListener {
    fun onReceive(message: ChatParser.Message)
}