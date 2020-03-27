package com.android.multistreamchat

import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_output_handler.ChatOutputHandler
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.input_handler.ChatInputHandler
import kotlinx.coroutines.channels.Channel

abstract class ChatManager(var emotemanager: EmotesManager<*, *>, var chatOutputHandler: ChatOutputHandler, var chatInputHandler: ChatInputHandler) {

    suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String) {
        chatOutputHandler.handleUserMessage(channel, message)
    }

    fun writeMessage(message: String) {
        chatInputHandler.sendMessage(message)
    }

    fun startInputConnection() {

    }

    fun startOutputConnection() {

    }

}