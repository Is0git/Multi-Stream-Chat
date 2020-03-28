package com.android.multistreamchat.socket

import com.android.multistreamchat.socket.chat_writer.WriterReaderHelper
import com.android.multistreamchat.user.User
import java.io.*
import java.net.Socket

abstract class ChatConnector(var host: String, var port: Int) {
    var socket: Socket? = null

    var isConnected: Boolean = false

    var isUserLoggedIn: Boolean = false

    val writerReaderHelper: WriterReaderHelper by lazy { WriterReaderHelper() }

    open fun connect(channelName: String) {
        this.socket = Socket(host, port).also {
            isConnected = true
            initStream()
        }
    }

    fun clear() {
        socket?.close().also { socket = null }
    }

    abstract fun initStream()

    abstract fun disconnect(writer: Writer?)
    abstract fun onConnected(writer: BufferedWriter?, reader: BufferedReader?,  user: User, channelName: String)
    abstract fun onFailed(message: String)
}