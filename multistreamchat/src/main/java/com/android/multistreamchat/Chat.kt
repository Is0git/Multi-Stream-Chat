package com.android.multistreamchat

import android.content.Context
import android.util.Log
import com.android.multistreamchat.chat_emotes.EmoteStateListener
import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_emotes.TwitchEmotesManager
import com.android.multistreamchat.chat_input_handler.TwitchInputHandler
import com.android.multistreamchat.chat_output_handler.TwitchOutputHandler
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.chat_parser.TwitchChatParser
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

class Chat private constructor(val host: String, val port: Int, var username: String? = null) :
    ChatStatesListener {

    private constructor(host: String, port: Int, username: String, token: String) : this(
        host,
        port,
        username
    ) {
        this.token = token
    }

    var channelName: String? = null

    var token: String? = null

    private val socket: Socket? by lazy { Socket(host, port) }

    private var dataListener: DataListener? = null

    private var chatManager: ChatManager? = null

    private var readSocket: Socket? = null

    private var writeSocket: Socket? = null

    companion object {
        const val HOST = "irc.chat.twitch.tv"
        const val PORT = 6667
    }

    fun connect(token: String?, name: String, channelName: String) {
        this.channelName = channelName
        CoroutineScope(Dispatchers.IO).launch {
            val channel = Channel<ChatParser.Message>(Channel.CONFLATED)
            try {
                val reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))
                writer.apply {
                    if (token != null) write("PASS oauth:${token}\n")
                    write("NICK $name\n")
                    write("CAP REQ :twitch.tv/tags\n")
                    write("CAP REQ :twitch.tv/commands\n")
                    write("JOIN #$channelName\n")
                    flush()
                }

                launch {
                    var line: String? = ""
                    while (line != null) {
                        line = reader.readLine()
                        line?.let {
                            Log.d("LINE", "$it")
                            when {
                                line.contains("privmsg", true) -> {
                                    chatManager?.handleUserMessage(channel, it)
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
                socket?.close()
            }

        }
    }

    fun typeMessage(message: String) {
        chatManager?.writeMessage(message)
    }

    fun getEmoteById(id: Int): TwitchEmotesManager.TwitchEmote {
        return (chatManager?.emoteManager as TwitchEmotesManager).globalEmotes[id]!!
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
        private var chatManager: ChatManager? = null
        var chatParser: ChatParser? = null
        val emoteStateListeners by lazy { mutableListOf<EmoteStateListener<*, *>>() }

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

        fun setChatManager(chatManager: ChatManager): Builder {
            this.chatManager = chatManager
            return this
        }

        fun addDataListener(dataListener: DataListener): Builder {
            this.dataListener = dataListener
            return this
        }

        inline fun <reified T : ChatParser> setChatParser(parserClass: Class<T>): Builder {
            val parse = when {
                parserClass.isAssignableFrom(TwitchChatParser::class.java) -> TwitchChatParser()
                else -> TwitchChatParser()
            }
            this.chatParser = parse
            return this
        }

        fun<K, E: EmotesManager.Emote> addEmoteStateListener(emoteStateListener: EmoteStateListener<K, E>) : Builder {
            emoteStateListeners.add(emoteStateListener)
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun build(context: Context): Chat {
            host ?: throw IllegalStateException("host was not set")
            port ?: throw IllegalStateException("port was not set")

            if (token == null && username == null) isAnonymous = true

            val chat = when {
                isAnonymous -> Chat(host!!, port!!, "justinfan12345")
                else -> Chat(host!!, port!!, username!!, token!!)
            }

            chat.apply {
                dataListener = this@Builder.dataListener
                chatManager = this@Builder.chatManager ?: ChatManager(
                    TwitchOutputHandler(
                        context,
                        TwitchChatParser(),
                        emoteStateListeners as List<EmoteStateListener<Int, TwitchEmotesManager.TwitchEmote>>
                    ),
                    TwitchInputHandler()
                )
                channelName = this@Builder.channelName
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