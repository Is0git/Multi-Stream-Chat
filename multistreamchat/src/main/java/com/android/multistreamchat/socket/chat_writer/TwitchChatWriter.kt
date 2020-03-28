package com.android.multistreamchat.socket.chat_writer

import com.android.multistreamchat.user.User
import java.io.Reader
import java.io.Writer

class TwitchChatWriter(host: String, port: Int, connectionWriterHelper: WriterReaderHelper) :
    ChatWriter(host, port, connectionWriterHelper) {
    override fun disconnect(writer: Writer?) {
    }

    override fun onConnected(writer: Writer?, reader: Reader?, user: User, channelName: String) {
        if (user.name != null && user.token != null) {
            writer?.apply {
                write("PASS oauth:${user.token}\n")
                write("NICK ${user.name}\n")
                flush()
            }.also { isUserLoggedIn = true }
        } else {
            onFailed("NEED TOKEN OR USERNAME")
        }
    }


    override fun onFailed(message: String) {
    }


}