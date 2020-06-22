package com.iso.chat.chat.socket

import com.iso.chat.chat.socket.chat_writer.WriterReaderHelper
import com.iso.chat.chat.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Writer
import java.net.Socket

abstract class ChatConnector(var host: String, var port: Int) {

    var socket: Socket? = null
    var isConnected: Boolean = false
    var isAnonymous = true
    var isUserLoggedIn: Boolean = false
    val writerReaderHelper: WriterReaderHelper by lazy { WriterReaderHelper() }
    var user: User? = null
    var channelName: String? = null
    var chatConnectivityListener: ChatConnectivityListener? = null
    var chatPlatformName: String = "unknown"

    constructor(host: String, port: Int, user: User?) : this(host, port) {
        this.user = user
    }

    open suspend fun connect(channelName: String) {
        coroutineScope {
            launch(Dispatchers.Default) {
                this@ChatConnector.channelName = channelName
                withContext(Dispatchers.IO) { socket = Socket(host, port) }
                initStream(channelName)
            }
        }
    }

    open fun clear() {
        socket?.close()
    }

    abstract suspend fun initStream(channelName: String)

    abstract fun disconnect(writer: Writer?)
    abstract suspend fun onConnected(
        writer: BufferedWriter?,
        reader: BufferedReader?,
        user: User?,
        channelName: String
    )
}