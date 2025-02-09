package com.iso.chat.chat.socket.chat_writer

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

open class WriterReaderHelper {
    var writer: BufferedWriter? = null
    var reader: BufferedReader? = null

    fun writeToIrcChat(message: String, channelName: String?) : Boolean {
        channelName ?: return false
        writer?.let {
            it.write("PRIVMSG #$channelName :$message\n")
            it.flush()
        }
        return true
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

    }
}