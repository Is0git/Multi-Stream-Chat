package com.android.multistreamchat

import android.util.Log
import com.android.multistreamchat.chat_output_handler.OutputHandler
import com.android.multistreamchat.chat_output_handler.TwitchOutputHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import kotlin.reflect.KClass

class Chat private constructor(val host: String, val port: Int, var username: String? = null) :
    ChatStatesListener {

    var token: String? = null

    private val socket: Socket by lazy { Socket(host, port) }

    private var dataListener: DataListener? = null

    lateinit var chatParser: ChatParser

    private var outputHandler: OutputHandler<ChatParser.Message>? = null

    private constructor(host: String, port: Int, username: String, token: String) : this(
        host,
        port,
        username
    ) {
        this.token = token

    }

    companion object {
        const val HOST = "irc.chat.twitch.tv"
        const val PORT = 6667
    }

    fun connect(token: String?, name: String, channelName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val channel = Channel<ChatParser.Message>(Channel.CONFLATED)
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

                writer.apply {
                    if (token != null) write("PASS oauth:${token}\n")
                    write("NICK $name\n")
                    write("JOIN #$channelName\n")
                    flush()
                }

                launch {
                    var line: String? = ""
                    while (line != null) {
                        line = reader.readLine()
                        line?.let {
                            when {
                                line.contains("privmsg", true) -> {
                                    outputHandler?.handleUserMessage(channel, it)
                                }
                                else -> return@let
                            }
                        }
                    }
                }

                for (a in channel) {
                    withContext(Dispatchers.Main) {
                        dataListener?.onReceive(a)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                channel.close()
                socket.close()
            }

        }
    }

    override fun onConnected() {
    }

    override fun onError() {

    }

    override fun onSend(line: String) {

    }

    class Builder {
        private var host: String? = null
        private var port: Int? = null
        private var isAnonymous: Boolean = false
        private var token: String? = null
        private var username: String? = null
        private var channelName: String? = null
        private var autoConnect: Boolean = false
        private var dataListener: DataListener? = null
        private var outputHandler: OutputHandler<ChatParser.Message>? = null
        var chatParser: ChatParser? = null

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

        fun setOutputHandler(outputHandler: OutputHandler<ChatParser.Message>) : Builder {
            this.outputHandler = outputHandler
            return this
        }

        fun addDataListener(dataListener: DataListener): Builder {
            this.dataListener = dataListener
            return this
        }


        inline fun <reified T : ChatParser> setChatParser(parserClass: Class<in T>): Builder {
            val parse = when {
                parserClass.isAssignableFrom(TwitchChatParser::class.java) -> TwitchChatParser()
                else -> TwitchChatParser()
            }
            this.chatParser = parse
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

            chat.apply {
                dataListener = this@Builder.dataListener
                chatParser = this@Builder.chatParser ?: TwitchChatParser()
                chat.outputHandler = this@Builder.outputHandler ?: TwitchOutputHandler()
                if (autoConnect && channelName != null) connect(
                    this.token,
                    this.username!!,
                    channelName!!
                )
            }

            return chat
        }
    }
}