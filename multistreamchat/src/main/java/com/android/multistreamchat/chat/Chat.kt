package com.android.multistreamchat.chat

import android.content.Context
import com.android.multistreamchat.chat.listeners.EmoteStateListener
import com.android.multistreamchat.chat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat.chat_emotes.TwitchEmotesManager
import com.android.multistreamchat.chat.chat_output_handler.ChatOutputHandler
import com.android.multistreamchat.chat.chat_output_handler.TwitchOutputHandler
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamchat.chat.chat_parser.TwitchChatParser
import com.android.multistreamchat.chat.input_handler.ChatInputHandler
import com.android.multistreamchat.chat.input_handler.TwitchInputHandler
import com.android.multistreamchat.chat.socket.chat_reader.ChatReader
import com.android.multistreamchat.chat.socket.chat_reader.TwitchChatReader
import com.android.multistreamchat.chat.socket.chat_writer.ChatWriter
import com.android.multistreamchat.chat.socket.chat_writer.TwitchChatWriter
import com.android.multistreamchat.chat.socket.chat_writer.WriterReaderHelper
import com.android.multistreamchat.chat.user.User
import com.android.multistreamchat.chat.listeners.DataListener
import com.android.multistreamchat.ChatManager

class Chat private constructor(val host: String, val port: Int, var username: String? = null) {

    private constructor(host: String, port: Int, username: String, token: String) : this(
        host,
        port,
        username
    ) {
        this.token = token
    }

    var channelName: String? = null

    var token: String? = null


    private var chatManager: ChatManager? = null

    companion object {
        const val HOST = "irc.chat.twitch.tv"
        const val PORT = 6667
    }

    fun connect(token: String?, name: String, channelName: String) {
        this.channelName = channelName
        chatManager?.connectReader(channelName)
        chatManager?.connectWriter(channelName)

    }

    fun typeMessage(message: String) {
        chatManager?.writeMessage(message)
    }

    fun getEmoteById(id: Int, set: String): TwitchEmotesManager.TwitchEmote {
        return (chatManager?.emoteManager as TwitchEmotesManager).globalEmotes[set]?.find{it.id == id} ?: throw NoSuchElementException("EMOTE WITH THIS ID WAS NOT FOUND")
    }

    class Builder {
        private var host: String? = null
        private var port: Int? = null
        private var isAnonymous: Boolean = false
        private var token: String? = null
        private var username: String? = null
        private var channelName: String? = null
        private var autoConnect: Boolean = false

        private var chatManager: ChatManager? = null
        private var chatWriter: ChatWriter? = null
        private var chatReader: ChatReader? = null
        private var emotesManager: EmotesManager<*, *>? = null
        private var writerReaderHelper: WriterReaderHelper? = null
        var chatParser: ChatParser? = null
        private var outputHandler: ChatOutputHandler? = null
        private var inputHandler: ChatInputHandler? = null

        private val dataListeners: MutableList<DataListener> by lazy { mutableListOf<DataListener>() }
        private val emoteStateListeners by lazy { mutableListOf<EmoteStateListener<*, *>>() }

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
            this.dataListeners.add(dataListener)
            return this
        }

        fun setChatReader(chatReader: ChatReader) : Builder {
            this.chatReader = chatReader
            return this
        }

        fun setChatWriter(chatWriter: ChatWriter) : Builder {
            this.chatWriter = chatWriter
            return this
        }

        fun setWriterReaderHelper(writerReaderHelper: WriterReaderHelper) : Builder {
            this.writerReaderHelper = writerReaderHelper
            return this
        }

        fun setChatInputHandler(inputHandler: ChatInputHandler) : Builder {
            this.inputHandler = inputHandler
            return this
        }

        fun setChatOutputHandler(outputHandler: ChatOutputHandler) : Builder {
            this.outputHandler = outputHandler
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

        fun <K, E : EmotesManager.Emote> addEmoteStateListener(emoteStateListener: EmoteStateListener<K, E>): Builder {
            emoteStateListeners.add(emoteStateListener)
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun build(context: Context): Chat {
            host ?: throw IllegalStateException("host was not set")
            port ?: throw IllegalStateException("port was not set")

            if (token == null && username == null) isAnonymous = true

            val chat = when {
                isAnonymous -> Chat(
                    host!!,
                    port!!,
                    "justinfan12345"
                )
                else -> Chat(
                    host!!,
                    port!!,
                    username!!,
                    token!!
                )
            }

            val user: User = User(chat.username, null, chat.token)

            val emotesManager = this.emotesManager ?: TwitchEmotesManager(context,  this.emoteStateListeners as List<EmoteStateListener<String, TwitchEmotesManager.TwitchEmote>>)

            val chatParser = this.chatParser ?: TwitchChatParser()

            val inputHandler = this.inputHandler ?: TwitchInputHandler(user, chat.host, chat.port)
            val outputHandler = this.outputHandler ?: TwitchOutputHandler(chatParser, emotesManager)

            val chatReader = this.chatReader ?: TwitchChatReader(chat.host, chat.port, user, outputHandler)
            chatReader.dataListeners = this.dataListeners

            val chatWriter = this.chatWriter ?: TwitchChatWriter(chat.host, chat.port, user, chat.channelName!!, inputHandler)
            val chatManager = this.chatManager ?: ChatManager(
                emotesManager,
                chatReader,
                chatWriter
            )

            chat.apply {
                this.chatManager = this@Builder.chatManager ?: chatManager
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