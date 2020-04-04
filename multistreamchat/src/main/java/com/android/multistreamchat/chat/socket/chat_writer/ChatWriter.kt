package com.android.multistreamchat.chat.socket.chat_writer

import com.android.multistreamchat.chat.input_handler.ChatInputHandler
import com.android.multistreamchat.chat.socket.ChatConnector
import com.android.multistreamchat.chat.user.User

abstract class ChatWriter(host: String, port: Int, user: User, var channelName: String?) : ChatConnector(host, port) {
    lateinit var chatInputHandler: ChatInputHandler
    constructor(host: String, port: Int,  user: User, channelName: String?, chatInputHandler: ChatInputHandler) : this(host, port, user, channelName) {
        this.chatInputHandler = chatInputHandler
    }

    override fun initStream(channelName: String) {
        socket?.apply {
            writerReaderHelper.setWriter(this)
            onConnected(writerReaderHelper.writer, writerReaderHelper.reader, user, channelName)
        }
    }

    fun writeMessage(message: String) {
        writerReaderHelper.writeToIrcChat(message,  channelName)
    }
}