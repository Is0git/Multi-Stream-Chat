package com.android.multistreamchat.chat_output_handler

import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_emotes.TwitchEmotesManager
import com.android.multistreamchat.chat_parser.ChatParser
import kotlinx.coroutines.channels.Channel

class TwitchOutputHandler(chatParser: ChatParser, emotesManager: EmotesManager<*,*>) : ChatOutputHandler(chatParser, emotesManager) {

    override suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String) {
        chatParser?.parseUserMessage(message).also {
            val spannableUserMessage = (emotesManager as TwitchEmotesManager).createsSpannable(
                it?.get("message") ?: "sdsd", chatParser?.extractEmoteIds(it!!["emotes"]))
            val msg = ChatParser.Message(
                it?.get("display-name")!!,
                it["message"]!!,
                it["display-name"]!!,
                it["color"] ?: "#000000",
                spannableUserMessage
            )
            channel.send(msg)
        }
    }
}