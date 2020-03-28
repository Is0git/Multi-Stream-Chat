package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.socket.chat_reader.ChatReader
import kotlinx.coroutines.channels.Channel
import java.net.Socket

abstract class ChatOutputHandler(var chatParser: ChatParser, var emotesManager: EmotesManager<*, *>)  {

    abstract suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String)
}