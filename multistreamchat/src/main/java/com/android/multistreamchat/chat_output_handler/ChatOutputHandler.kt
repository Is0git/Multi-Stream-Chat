package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_parser.ChatParser
import kotlinx.coroutines.channels.Channel
import java.net.Socket

abstract class ChatOutputHandler(var chatParser: ChatParser?, var emotesManager: EmotesManager<*, *>)  {
    var readSocket: Socket? = null

    abstract suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String)
}