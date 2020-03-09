package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.ChatParser
import kotlinx.coroutines.channels.Channel

interface OutputHandler<T> {
   suspend fun handleUserMessage(channel: Channel<T>, message: String)
}