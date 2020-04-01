package com.android.multistreamchat.twitch_chat.output_handler

import com.android.multistreamchat.chat.badges.BadgesManager
import com.android.multistreamchat.chat.chat_emotes.EmotesManager
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.android.multistreamchat.chat.chat_output_handler.ChatOutputHandler
import com.android.multistreamchat.chat.chat_parser.ChatParser
import kotlinx.coroutines.channels.Channel

class TwitchOutputHandler(chatParser: ChatParser, emotesManager: EmotesManager<*,*>, badgesManager: BadgesManager<*>) : ChatOutputHandler(chatParser, emotesManager, badgesManager) {

    override suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, rawMessage: String) {

        chatParser.parseUserMessage(rawMessage).also { parsedIrcMessage ->

            val extractedEmoteIds = emotesManager.extractEmoteIds(parsedIrcMessage["emotes"])

            val badgesCodes = chatParser.parseBadgesFromMessage(parsedIrcMessage["badges"])

            val msg = ChatParser.Message(
                parsedIrcMessage["display-name"]!!,
                parsedIrcMessage["message"]!!,
                parsedIrcMessage["display-name"]!!,
                parsedIrcMessage["color"] ?: "#000000",
                extractedEmoteIds?.let { (emotesManager as TwitchEmotesManager).createEmoteSpannable(parsedIrcMessage["message"]!!, (emotesManager as TwitchEmotesManager).getMessageEmoteCodes(
                    extractedEmoteIds
                )) },
                badgesManager.getMessageBadges(badgesCodes)
            )
            channel.send(msg)
        }
    }
}