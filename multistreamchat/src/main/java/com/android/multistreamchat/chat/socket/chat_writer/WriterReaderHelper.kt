package com.android.multistreamchat.chat.socket.chat_writer

import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.Socket

open class WriterReaderHelper {
    var writer: BufferedWriter? = null

    var reader: BufferedReader? = null

    fun writeToIrcChat(message: String, channelName: String) {
        try {
            writer?.let {
                it.write("PRIVMSG #$channelName :$message\n")
                it.flush()
            }
        } catch (exception: Exception) {
            Log.d("WRITERHELPER", exception.message)
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