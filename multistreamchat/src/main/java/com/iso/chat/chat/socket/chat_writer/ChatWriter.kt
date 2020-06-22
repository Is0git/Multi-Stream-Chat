package com.iso.chat.chat.socket.chat_writer

import com.iso.chat.chat.input_handler.ChatInputHandler
import com.iso.chat.chat.socket.ChatConnector
import com.iso.chat.chat.user.User

abstract class ChatWriter(host: String, port: Int, user: User,  channelName: String?) : ChatConnector(host, port, user) {
    lateinit var chatInputHandler: ChatInputHandler
    constructor(host: String, port: Int,  user: User, channelName: String?, chatInputHandler: ChatInputHandler) : this(host, port, user, channelName) {
        this.chatInputHandler = chatInputHandler
    }

    override suspend fun initStream(channelName: String) {
        socket?.apply {
            writerReaderHelper.setWriter(this)
            this@ChatWriter.isConnected = true
            onConnected(writerReaderHelper.writer, writerReaderHelper.reader, user, channelName)
        }
    }

    fun writeMessage(message: String) : Boolean {
       return writerReaderHelper.writeToIrcChat(message,  channelName)
    }
}