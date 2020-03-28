package com.android.multistreamchat.input_handler

import com.android.multistreamchat.socket.chat_writer.ChatWriter
import com.android.multistreamchat.user.User

abstract class ChatInputHandler(user: User, host: String, port: Int, private val chatWriter: ChatWriter) {

    fun sendMessage(message: String) {
        chatWriter.writeMessage(message)
    }
}