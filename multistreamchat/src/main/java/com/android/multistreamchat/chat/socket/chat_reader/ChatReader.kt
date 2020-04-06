package com.android.multistreamchat.chat.socket.chat_reader

import com.android.multistreamchat.chat.listeners.DataListener
import com.android.multistreamchat.chat.chat_output_handler.ChatOutputHandler
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamchat.chat.socket.ChatConnector
import com.android.multistreamchat.chat.user.User
import kotlinx.coroutines.channels.Channel

abstract class ChatReader(host: String, port: Int, user: User?) : ChatConnector(host, port, user) {

    lateinit var chatOutputHandler: ChatOutputHandler

    var dataListeners: MutableList<DataListener>? = null

    constructor(host: String, port: Int, user: User?, chatOutputHandler: ChatOutputHandler, channelName: String?) : this(host, port, user) {
        this.chatOutputHandler = chatOutputHandler
        channelName?.let { chatOutputHandler?.badgesManager.getAllBadges(it) }
    }

    override fun initStream(channelName: String) {
        socket?.apply {
            writerReaderHelper.setWriterAndReader(this)
            onConnected(writerReaderHelper.writer, writerReaderHelper.reader, user, channelName)
        }
    }
    suspend fun handleUserMessage(channel: Channel<ChatParser.Message>, message: String) {
        chatOutputHandler.handleUserMessage(channel, message)
    }


}