package com.android.multistreamchat.input_handler

import com.android.multistreamchat.user.User
import java.io.*
import java.net.Socket

abstract class ChatInputHandler(var user: User, host: String, port: Int) {

    var writeSocket: Socket? = null

    var outputWriter: OutputStreamWriter? = null

    fun sendMessage(message: String) {
        outputWriter?.write(message).also { outputWriter?.flush() }
    }

    fun connect(socket: Socket, channelName: String) {
        if (user.name != null && user.token != null) {
            writeSocket = socket.also { outputWriter = OutputStreamWriter(it.getOutputStream()) }
            outputWriter?.let { onConnected(it, user, channelName) }
        } else {
            onFailed("NEED TOKEN OR USERNAME")
        }
    }

    fun clear() {
        outputWriter?.close().also { outputWriter = null }
        writeSocket?.close().also { writeSocket = null }
    }

    abstract fun onConnected(writer: Writer, user: User, channelName: String)
    abstract fun onFailed(message: String)
}