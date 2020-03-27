package com.android.multistreamchat.input_handler

import com.android.multistreamchat.user.User
import java.io.OutputStream
import java.io.Writer

class TwitchInputHandler(user: User, host: String, port: Int) : ChatInputHandler(user, host, port) {
    override fun onConnected(writer: Writer, user: User, channelName: String) {
        writer.apply {
            write("NICK ${user.name}\n")
            write("PASS oauth:${user.token}\n")
            write("JOIN #$channelName\n")
            flush()
        }
    }

    override fun onFailed(message: String) {

    }

}