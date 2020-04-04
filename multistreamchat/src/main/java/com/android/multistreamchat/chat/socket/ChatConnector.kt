package com.android.multistreamchat.chat.socket

import com.android.multistreamchat.chat.socket.chat_writer.WriterReaderHelper
import com.android.multistreamchat.chat.user.User
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Writer
import java.net.Socket

abstract class ChatConnector(var host: String, var port: Int) {
    var socket: Socket? = null

    var isConnected: Boolean = false

    var isUserLoggedIn: Boolean = false

    val writerReaderHelper: WriterReaderHelper by lazy { WriterReaderHelper() }

    var user: User? = null

    var channelName: String? = null

    constructor(host: String, port: Int, user: User?) : this(host, port) {
        this.user = user
    }

    open fun connect(channelName: String) {
        this.channelName = channelName
        this.socket = Socket(host, port)
        isConnected = true
        initStream(channelName)
    }

    fun clear() {
        socket?.close().also { socket = null }
    }

    abstract fun initStream(channelName: String)

    abstract fun disconnect(writer: Writer?)
    abstract fun onConnected(writer: BufferedWriter?, reader: BufferedReader?,  user: User?, channelName: String)
    abstract fun onFailed(message: String)
}