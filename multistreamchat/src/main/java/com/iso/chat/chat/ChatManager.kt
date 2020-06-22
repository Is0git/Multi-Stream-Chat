package com.iso.chat.chat

import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.chat.chat.socket.chat_reader.ChatReader
import com.iso.chat.chat.socket.chat_writer.ChatWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class
ChatManager(
    var emoteManager: EmotesManager<*, *>,
    var chatReader: ChatReader,
    var chatWriter: ChatWriter
) {
    private var writeMessageJob: Job? = null
    var platformName: String = ""

    fun writeMessage(message: String) {
        writeMessageJob = CoroutineScope(Dispatchers.Default).launch {
            chatWriter.writeMessage(message)
        }
    }

    suspend fun connectWriter(channelName: String) {
            chatWriter.connect(channelName)
    }

    suspend fun connectReader(channelName: String) {
            chatReader.connect(channelName)
    }

    fun getGlobalEmotes(): MutableMap<out Any?, out List<EmotesManager.Emote>> {
        return emoteManager.globalEmotes
    }

    fun getCurrentChatFlag() : Int {
        return chatReader.getCurrentChatFlag()
    }

    fun getCurrentChatFlagSet() : Int {
        return chatReader.getCurrentChatFlagSet()
    }

    fun getFollowersOnlyTime() : Int {
        return chatReader.getFollowersOnlyTime()
    }

    fun getSlowChatTime() : Int {
        return chatReader.getSlowChatTime()
    }

    fun clear() {
        writeMessageJob?.cancel().also { writeMessageJob = null }
        emoteManager.clear()
        chatReader.clear()
        chatWriter.clear()
    }
}