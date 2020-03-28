package com.android.multistreamchat.socket.chat_writer

import java.io.*
import java.net.Socket

open class WriterReaderHelper {
    var writer: BufferedWriter? = null

    var reader: BufferedReader? = null

    fun writeToIrcChat(message: String) {
        writer?.let {
            it.write(message)
            it.flush()
        }
    }

    fun setReader(socket: Socket) {
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    fun setWriter(socket: Socket) {
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    }

    fun setWriterAndReader(socket: Socket) {
        setReader(socket)
        setWriter(socket)
    }

    fun disconnect() {
        writer?.close().also { writer = null }
    }
}