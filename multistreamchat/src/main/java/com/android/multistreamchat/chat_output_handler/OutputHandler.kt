package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_parser.ChatParser
import kotlinx.coroutines.channels.Channel

interface OutputHandler {

   var emoteManager: EmotesManager<*, *>


   suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String)
}