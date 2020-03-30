package com.android.multistreamchat.chat.socket

import java.net.Socket

object SocketFactory {
    const val TWITCH: Int = 0
    const val MIXERL: Int = 1

    fun createSocket(host: String, port: Int): Socket {
        val socket = Socket(host, port)

        return socket
    }
}