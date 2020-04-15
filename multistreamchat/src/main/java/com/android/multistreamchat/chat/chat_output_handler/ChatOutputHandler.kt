package com.android.multistreamchat.chat.chat_output_handler

import com.android.multistreamchat.chat.badges.BadgesManager
import com.android.multistreamchat.chat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat.chat_parser.ChatParser
import kotlinx.coroutines.channels.Channel

abstract class ChatOutputHandler(var chatParser: ChatParser, var emotesManager: EmotesManager<*, *>, var badgesManager: BadgesManager<*>)  {

    abstract suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, rawMessage: String)

    fun clear() {
        badgesManager.clear()
    }
}