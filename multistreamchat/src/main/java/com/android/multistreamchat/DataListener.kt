package com.android.multistreamchat

import com.android.multistreamchat.chat_parser.ChatParser

interface DataListener {
    fun onReceive(message: ChatParser.Message)
}