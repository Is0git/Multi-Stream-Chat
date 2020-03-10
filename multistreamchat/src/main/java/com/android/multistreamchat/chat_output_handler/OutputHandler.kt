package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.ChatParser
import kotlinx.coroutines.channels.Channel

interface OutputHandler {
   suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String)
}