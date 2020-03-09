package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.ChatParser
import com.android.multistreamchat.TwitchChatParser
import kotlinx.coroutines.channels.Channel

class TwitchOutputHandler : OutputHandler<ChatParser.Message>, TwitchChatParser() {
    override suspend fun handleUserMessage(channel: Channel<Message>, message: String) {
        parseUserMessage(message).also {
            val msg = Message(it[1], it.last(), it[3])
            channel.send(msg)
        }
    }
}