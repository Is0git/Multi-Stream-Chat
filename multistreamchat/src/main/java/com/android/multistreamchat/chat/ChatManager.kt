package com.android.multistreamchat.chat

import com.android.multistreamchat.chat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat.socket.chat_reader.ChatReader
import com.android.multistreamchat.chat.socket.chat_writer.ChatWriter
import kotlinx.coroutines.*

class
ChatManager(var emoteManager: EmotesManager<*, *>, var chatReader: ChatReader, var chatWriter: ChatWriter) {

    var writerJob: Job? = null
    var readerJob: Job? = null
    var writeMessageJob: Job? = null

    fun writeMessage(message: String) {
       writeMessageJob = CoroutineScope(Dispatchers.Default).launch {
           chatWriter.writeMessage(message)
       }
    }

    fun connectWriter(channelName: String) {
        writerJob = CoroutineScope(Dispatchers.Default).launch {
            chatWriter.connect(channelName)
        }
    }

    fun connectReader(channelName: String) {
        readerJob = CoroutineScope(Dispatchers.Default).launch {
            chatReader.connect(channelName)
        }
    }

    fun getGlobalEmotes():MutableMap<out Any?, out List<EmotesManager.Emote>>  {
        return emoteManager.globalEmotes
    }

    fun clear() {
        writerJob?.cancel().also { writerJob = null }
        readerJob?.cancel().also { readerJob = null }
        writeMessageJob?.cancel().also { writeMessageJob = null }
    }
}