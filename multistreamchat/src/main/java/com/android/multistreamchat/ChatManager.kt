package com.android.multistreamchat

import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.socket.chat_reader.ChatReader
import com.android.multistreamchat.socket.chat_writer.ChatWriter

abstract class ChatManager(var emoteManager: EmotesManager<*, *>, var chatReader: ChatReader, var chatWriter: ChatWriter) {

    fun writeMessage(message: String) {
        chatWriter.writeMessage(message)
    }

    fun connectWriter(channelName: String) {
        chatWriter.connect(channelName)
    }

    fun connectReader(channelName: String) {
        chatReader.connect(channelName)
    }

}