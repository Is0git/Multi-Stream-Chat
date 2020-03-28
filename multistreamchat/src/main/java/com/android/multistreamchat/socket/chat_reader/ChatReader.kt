package com.android.multistreamchat.socket.chat_reader

import com.android.multistreamchat.DataListener
import com.android.multistreamchat.chat_output_handler.ChatOutputHandler
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.socket.ChatConnector
import com.android.multistreamchat.socket.chat_writer.WriterReaderHelper
import com.android.multistreamchat.user.User
import kotlinx.coroutines.channels.Channel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader

abstract class ChatReader(host: String, port: Int) : ChatConnector(host, port) {

    lateinit var chatOutputHandler: ChatOutputHandler

    val dataListeners: MutableList<DataListener> by lazy { mutableListOf<DataListener>() }

    constructor(host: String, port: Int, chatOutputHandler: ChatOutputHandler) : this(host, port) {
        this.chatOutputHandler = chatOutputHandler
    }

    override fun initStream() {
        socket?.apply {
            writerReaderHelper.setWriterAndReader(this)
        }
    }
    suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String) {
        chatOutputHandler.handleUserMessage(channel, message)
    }

    abstract fun onConnected(
        writer: BufferedWriter?,
        reader: BufferedReader?,
        user: User,
        channelName: String
    )
}