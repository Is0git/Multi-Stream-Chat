package com.android.multistreamchat

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

    class Chat private constructor( val host: String,  val port: Int, var username: String? = null) : ChatStatesListener {

     var token: String? = null

    private val socket: Socket by lazy { Socket(host, port) }

    private constructor(host: String, port: Int, username: String, token: String) : this(host,  port,  username) {
        this.token = token

    }

    companion object {
        const val HOST = "irc.chat.twitch.tv"
        const val PORT = 6667
    }

    fun connect(token: String?, name: String, channelName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                writer.apply {
                 if (token != null)   write("PASS oauth:${token}\n")
                    write("NICK $name\n")
                    write("JOIN #$channelName\n")
                    flush()
                }

                var line: String? = ""
                while (line != null) {
                    line = reader.readLine()
                    Log.d("LINE", "$line")
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onConnected() {
    }

    override fun onError() {

    }

    class Builder {
        private var host: String? = null
        private var port: Int? = null
        private var isAnonymous: Boolean = false
        private var token: String? = null
        private var username: String? = null
        private var channelName: String? = null
        private var autoConnect: Boolean = false

        fun setHost(host: String): Builder {
            this.host = host
            return this
        }

        fun setPort(port: Int): Builder {
            this.port = port
            return this
        }

        fun setClient(host: String, port: Int): Builder {
            this.host = host
            this.port = port
            return this
        }

        fun setUserToken(token: String): Builder {
            this.token = token
            return this
        }

        fun setUsername(username: String): Builder {
            this.username = username
            return this
        }

        fun autoConnect(channelName: String): Builder {
            this.autoConnect = true
            this.channelName = channelName
            return this
        }

        fun build(): Chat {
            host ?: throw IllegalStateException("host was not set")
            port ?: throw IllegalStateException("port was not set")

            if (token == null && username == null) isAnonymous = true

            val chat = when {
                isAnonymous -> Chat(host!!, port!!, "justinfan12345")
                else -> Chat(host!!, port!!, username!!, token!!)
            }
            if (autoConnect && channelName != null) chat.apply {
                connect(this.token, this.username!!, channelName!!)
            }
            return chat
        }
    }
}