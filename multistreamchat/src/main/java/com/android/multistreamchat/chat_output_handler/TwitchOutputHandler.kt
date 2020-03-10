package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.ChatParser
import com.android.multistreamchat.TwitchChatParser
import kotlinx.coroutines.channels.Channel

class TwitchOutputHandler : OutputHandler, TwitchChatParser() {
    override suspend fun handleUserMessage(channel: Channel<Message>, message: String) {
        parseUserMessage(message).also {
            val msg = Message(it["display-name"]!!, it["display-name"]!!, it["display-name"]!!, it["color"] ?: "#000000" )
            channel.send(msg)
        }
    }
}