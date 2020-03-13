package com.android.multistreamchat.chat_output_handler

import android.content.Context
import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_emotes.TwitchEmoteManager
import com.android.multistreamchat.chat_parser.TwitchChatParser
import kotlinx.coroutines.channels.Channel

class TwitchOutputHandler(val context: Context) : OutputHandler, TwitchChatParser() {
    override var emoteManager: EmotesManager<*, *> = TwitchEmoteManager(context)

    override suspend fun handleUserMessage(channel: Channel<Message>, message: String) {
        parseUserMessage(message).also {
            val msg = Message(it["display-name"]!!, it["message"]!!, it["display-name"]!!, it["color"] ?: "#000000")
            channel.send(msg)
        }
    }
}