package com.iso.chat.chat

import android.content.Context
import android.util.Log
import com.iso.chat.chat.badges.BadgesManager
import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler
import com.iso.chat.chat.chat_parser.ChatParser
import com.iso.chat.chat.input_handler.ChatInputHandler
import com.iso.chat.chat.listeners.DataListener
import com.iso.chat.chat.listeners.EmoteStateListener
import com.iso.chat.chat.socket.ChatConnectivityListener
import com.iso.chat.chat.socket.chat_reader.ChatReader
import com.iso.chat.chat.socket.chat_reader.TwitchChatReader
import com.iso.chat.chat.socket.chat_writer.ChatWriter
import com.iso.chat.chat.socket.chat_writer.TwitchChatWriter
import com.iso.chat.chat.socket.chat_writer.WriterReaderHelper
import com.iso.chat.chat.user.User
import com.iso.chat.twitch_chat.badges.TwitchBadgesManager
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.iso.chat.twitch_chat.chat_parser.TwitchChatParser
import com.iso.chat.twitch_chat.input_handler.TwitchInputHandler
import com.iso.chat.twitch_chat.output_handler.OnRoomStateListener
import com.iso.chat.twitch_chat.output_handler.TwitchOutputHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class Chat private constructor(
    var context: Context,
    val host: String,
    val port: Int,
    var username: String? = null
) {
    companion object {
        const val HOST = "irc.chat.twitch.tv"
        const val PORT = 6667
        const val TAG = "CHAT_TAG"
    }

    private constructor(
        context: Context,
        host: String,
        port: Int,
        username: String,
        token: String
    ) : this(
        context,
        host,
        port,
        username
    ) {
        this.token = token
    }

    var channelName: String? = null
    var token: String? = null
    var writerJob: Job? = null
    var readerJob: Job? = null
    lateinit var chatManager: ChatManager

    fun connect(token: String?, name: String, channelName: String) {
        this.channelName = channelName
        connectReader(channelName)
        connectWriter(channelName)
    }

    private fun connectWriter(channelName: String) {
        writerJob =
            CoroutineScope(Dispatchers.Main).launch(chatManager.chatReader.socketExceptionHandler) {
                chatManager.connectWriter(channelName)
            }
        writerJob?.invokeOnCompletion { it ->
        }
    }

    private fun connectReader(channelName: String) {
        readerJob =
            CoroutineScope(Dispatchers.Main).launch(chatManager.chatReader.socketExceptionHandler) {
                chatManager.connectReader(channelName)
            }
        readerJob?.invokeOnCompletion { throwable ->
            when {
                throwable?.message == "Software caused connection abort" -> {
                    chatManager.chatReader.chatConnectivityListener?.onConnectivityStateChange(
                        false,
                        chatManager.platformName
                    )
                    reconnect()
                }
                throwable is UnknownHostException -> {
                    reconnect()
                }
                else -> {
                    Log.d(TAG, "READER CAUGHT ${throwable?.message}")
                }
            }
        }

    }

    fun reconnect() {
        channelName?.also {
            connectReader(it)
            connectWriter(it)
        }
    }

    fun disconnect() {
        chatManager.chatReader.socket?.close()
        chatManager.chatWriter.socket?.close()
        writerJob?.cancel()
        readerJob?.cancel()
        chatManager.chatReader.chatConnectivityListener?.onConnectivityStateChange(
            false,
            chatManager.platformName
        )
    }

    fun clear() {
        readerJob?.cancel()
        writerJob?.cancel()
        chatManager.clear()
    }

    fun typeMessage(message: String) {
        chatManager.writeMessage(message)
    }

    fun getCurrentChatFlag() : Int {
        return chatManager.getCurrentChatFlag()
    }

    fun getCurrentChatFlagSet() : Int {
        return chatManager.getCurrentChatFlagSet()
    }

    fun getFollowersOnlyTime() : Int {
        return chatManager.getFollowersOnlyTime()
    }

    fun getSlowChatTime() : Int {
        return chatManager.getSlowChatTime()
    }

    fun getEmoteById(id: Int, set: String): TwitchEmotesManager.TwitchEmote {
        return (chatManager.emoteManager as TwitchEmotesManager).globalEmotes[set]?.find { it.id == id }
            ?: throw NoSuchElementException("EMOTE WITH THIS ID WAS NOT FOUND")
    }

    fun getAllEmotes(): MutableMap<out Any?, out List<EmotesManager.Emote>>? {
        return chatManager.getGlobalEmotes()
    }

    class Builder {
        private var host: String? = null
        private var port: Int? = null
        private var isAnonymous: Boolean = false
        private var token: String? = null
        private var username: String? = null
        private var channelName: String? = null
        private var chatPlatformName: String = "unknown"
        private var autoConnect: Boolean = false
        private var chatManager: ChatManager? = null
        private var chatWriter: ChatWriter? = null
        private var chatReader: ChatReader? = null
        private var emotesManager: EmotesManager<*, *>? = null
        private var writerReaderHelper: WriterReaderHelper? = null
        var chatParser: ChatParser? = null
        private var outputHandler: ChatOutputHandler? = null
        private var inputHandler: ChatInputHandler? = null
        private var badgesManager: BadgesManager<*>? = null
        private var connectivityListener: ChatConnectivityListener? = null
        private val dataListeners: MutableList<DataListener> by lazy { mutableListOf<DataListener>() }
        private val emoteStateListeners by lazy { mutableListOf<EmoteStateListener<*, *>>() }
        private var onRoomStateListener: OnRoomStateListener? = null

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

        fun setChatReader(chatReader: ChatReader): Builder {
            this.chatReader = chatReader
            return this
        }

        fun setChatWriter(chatWriter: ChatWriter): Builder {
            this.chatWriter = chatWriter
            return this
        }

        fun setWriterReaderHelper(writerReaderHelper: WriterReaderHelper): Builder {
            this.writerReaderHelper = writerReaderHelper
            return this
        }

        fun setChatInputHandler(inputHandler: ChatInputHandler): Builder {
            this.inputHandler = inputHandler
            return this
        }

        fun addChatConnectivityListener(connectivityListener: ChatConnectivityListener): Builder {
            this.connectivityListener = connectivityListener
            return this
        }

        fun setChatOutputHandler(outputHandler: ChatOutputHandler): Builder {
            this.outputHandler = outputHandler
            return this
        }

        fun setBadgeManager(badgesManager: BadgesManager<*>): Builder {
            this.badgesManager = badgesManager
            return this
        }

        fun setPlatformName(name: String): Builder {
            chatPlatformName = name
            return this
        }

        fun setOnRoomStateListener(roomStateListener: OnRoomStateListener?) : Builder {
            this.onRoomStateListener = roomStateListener
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
                    context,
                    host!!,
                    port!!,
                    "justinfan1337"
                )
                else -> Chat(
                    context,
                    host!!,
                    port!!,
                    username!!,
                    token!!
                )
            }
            val user = User(chat.username, null, chat.token)
            val emotesManager = this.emotesManager ?: TwitchEmotesManager(
                context,
                this.emoteStateListeners as List<EmoteStateListener<String, TwitchEmotesManager.TwitchEmote>>
            )
            val chatParser = this.chatParser ?: TwitchChatParser()
            val badgesManager = badgesManager ?: TwitchBadgesManager(context)
            val inputHandler = this.inputHandler ?: TwitchInputHandler(user, chat.host, chat.port)
            val outputHandler =
                this.outputHandler ?: TwitchOutputHandler(chatParser, emotesManager, badgesManager).also { it.onRoomStateListener = this.onRoomStateListener }
            outputHandler.dataListeners = this.dataListeners
            val chatReader = this.chatReader ?: TwitchChatReader(
                chat.host,
                chat.port,
                user,
                outputHandler,
                channelName
            )
            val chatWriter = this.chatWriter ?: TwitchChatWriter(
                chat.host,
                chat.port,
                user,
                chat.channelName,
                inputHandler
            )
            chatWriter.apply {
                chatConnectivityListener = connectivityListener
                chatPlatformName = this@Builder.chatPlatformName
            }
            chatReader.chatConnectivityListener = this.connectivityListener
            val chatManager = this.chatManager ?: ChatManager(
                emotesManager,
                chatReader,
                chatWriter
            )
            chatManager.platformName = this.chatPlatformName
            chat.apply {
                this.chatManager = this@Builder.chatManager ?: chatManager
                channelName = this@Builder.channelName
                    ?: throw KotlinNullPointerException("channel name cant be null")
                if (autoConnect) connect(
                    this.token,
                    this.username!!,
                    channelName!!
                )
            }
            return chat
        }
    }
}