package com.android.multistreamchat.socket

import com.android.multistreamchat.user.User
import java.io.OutputStreamWriter
import java.io.Writer
import java.net.Socket

abstract class ChatConnector {
    var socket: Socket? = null
    abstract fun connect(socket: Socket, channelName: String)

    fun clear() {
        socket?.close().also { socket = null }
    }

    abstract fun onConnected(writer: Writer, user: User, channelName: String)
    abstract fun onFailed(message: String)
}