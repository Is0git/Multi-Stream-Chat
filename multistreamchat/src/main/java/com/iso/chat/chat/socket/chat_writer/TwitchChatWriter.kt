package com.iso.chat.chat.socket.chat_writer

import com.iso.chat.chat.input_handler.ChatInputHandler
import com.iso.chat.chat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Writer

class TwitchChatWriter(
    host: String,
    port: Int,
    user: User,
    channelName: String?,
    inputHandler: ChatInputHandler
) :
    ChatWriter(host, port, user, channelName, inputHandler) {
    override fun disconnect(writer: Writer?) {
    }

    override suspend fun onConnected(
        writer: BufferedWriter?,
        reader: BufferedReader?,
        user: User?,
        channelName: String
    ) {
        if (user?.name != null && user.token != null) {
            if (user.name == "justinfan12345" || user.token == null) withContext(Dispatchers.Main) {
                chatConnectivityListener?.onChatModeChange(
                    true
                )
            }
            writer?.apply {
                write("PASS oauth:${user.token}\n")
                write("NICK ${user.name}\n")
                flush()
            }.also { isUserLoggedIn = true }
        }

    }

}