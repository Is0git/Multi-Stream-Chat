package com.iso.chat.chat.chat_output_handler

import com.iso.chat.chat.badges.BadgesManager
import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.chat.chat.chat_parser.ChatParser
import com.iso.chat.chat.listeners.DataListener

abstract class ChatOutputHandler(var chatParser: ChatParser, var emotesManager: EmotesManager<*, *>, var badgesManager: BadgesManager<*>)  {
    companion object {
        const val NONE: Int = 0
        const val FOLLOWERS_ONLY: Int = 1
        const val R9K_MODE: Int = 2
        const val SLOW_MODE: Int = 4
        const val SUB_MODE: Int = 8
        const val ONLY_EMOTES_MODE: Int = 16
        const val ALL: Int = 32
    }

    var dataListeners: MutableList<DataListener>? = null
    var currentFlagSet = NONE
    var followersOnlyTime = 0
    var slowChatTime = 0
    var currentFlag = NONE

    abstract suspend fun handleUserMessage(rawMessage: String)
    abstract  suspend fun handleRoomStateChange(rawMessage: String)
    abstract  suspend fun handleServerMessage(rawMessage: String, channelName: String)
    fun clear() {
        badgesManager.clear()
        dataListeners = null
    }
}