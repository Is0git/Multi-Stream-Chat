package com.android.multistreamchat.socket

import com.android.multistreamchat.user.User
import java.io.OutputStreamWriter
import java.io.Writer
import java.net.Socket

class TwitchWriteConnection(val user: User) : ChatConnector() {

    lateinit var outputWriter: OutputStreamWriter

    override fun connect(socket: Socket, channelName: String) {
        if (user.name != null && user.token != null) {
            this.socket = socket.also { outputWriter = OutputStreamWriter(it.getOutputStream()) }
            outputWriter?.let { onConnected(it, user, channelName) }
        } else {
            onFailed("NEED TOKEN OR USERNAME")
        }
    }

    override fun onConnected(writer: Writer, user: User, channelName: String) {

    }

    override fun onFailed(message: String) {
        
    }
}