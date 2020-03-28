package com.android.multistreamchat.socket.chat_writer

import com.android.multistreamchat.socket.ChatConnector
import java.io.*

abstract class ChatWriter(host: String, port: Int) : ChatConnector(host, port) {

    override fun initStream() {
        socket?.apply {
            writerReaderHelper.setWriter(this)
        }
    }

    fun writeMessage(message: String) {
        writerReaderHelper.writeToIrcChat(message)
    }
}