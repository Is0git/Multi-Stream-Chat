package com.android.multistreamchat.chat.socket.chat_writer

import android.util.Log
import com.android.multistreamchat.chat.input_handler.ChatInputHandler
import com.android.multistreamchat.chat.user.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Writer

class TwitchChatWriter(
    host: String,
    port: Int,
    user: User,
    inputHandler: ChatInputHandler
) :
    ChatWriter(host, port, user,  inputHandler) {
    override fun disconnect(writer: Writer?) {
    }

    override fun onConnected(
        writer: BufferedWriter?,
        reader: BufferedReader?,
        user: User?,
        channelName: String
    ) {
        if (user?.name != null && user.token != null) {
            writer?.apply {
                write("PASS oauth:${user.token}\n")
                write("NICK ${user.name}\n")
                write("JOIN #$channelName\n")
                flush()
            }.also { isUserLoggedIn = true }
        } else {
            onFailed("NEED TOKEN OR USERNAME")
        }
    }

    override fun onFailed(message: String) {
    }


}