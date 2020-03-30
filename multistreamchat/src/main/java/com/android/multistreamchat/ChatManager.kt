package com.android.multistreamchat

import com.android.multistreamchat.chat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat.socket.chat_reader.ChatReader
import com.android.multistreamchat.chat.socket.chat_writer.ChatWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatManager(var emoteManager: EmotesManager<*, *>, var chatReader: ChatReader, var chatWriter: ChatWriter) {

    var writerJob: Job? = null
    var readerJob: Job? = null

    fun writeMessage(message: String) {
        chatWriter.writeMessage(message)
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


    fun clear() {
        writerJob?.cancel().also { writerJob = null }
        readerJob?.cancel().also { readerJob = null }
    }
}