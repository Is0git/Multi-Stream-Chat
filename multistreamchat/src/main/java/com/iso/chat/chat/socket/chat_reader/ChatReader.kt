package com.iso.chat.chat.socket.chat_reader

import android.util.Log
import com.iso.chat.chat.Chat
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler
import com.iso.chat.chat.socket.ChatConnector
import com.iso.chat.chat.user.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel

abstract class ChatReader(host: String, port: Int, user: User?) : ChatConnector(host, port, user) {

    lateinit var chatOutputHandler: ChatOutputHandler
    var channel: Channel<*>? = null
     var socketExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d(Chat.TAG, "socket throw: $throwable")
        socket?.close()
        channel?.close()
    }
    fun getExceptionHandler() : CoroutineExceptionHandler {
        return CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d(Chat.TAG, "socket throw: $throwable")
            socket?.close()
            channel?.close()
        }
    }
    constructor(host: String, port: Int, user: User?, chatOutputHandler: ChatOutputHandler, channelName: String?) : this(host, port, user) {
        this.chatOutputHandler = chatOutputHandler
        channelName?.let { chatOutputHandler.badgesManager.getAllBadges(it) }
    }

    override suspend fun initStream(channelName: String) {
        socket?.apply {
            writerReaderHelper.setWriterAndReader(this)
            onConnected(writerReaderHelper.writer, writerReaderHelper.reader, user, channelName)
        }
    }
    suspend fun handleUserMessage(message: String) {
        chatOutputHandler.handleUserMessage(message)
    }

    suspend fun handleRoomStateChange(message: String) {
        chatOutputHandler.handleRoomStateChange(message)
    }

    suspend fun handleServerMessage(message: String, channelName: String) {
        chatOutputHandler.handleServerMessage(message, channelName)
    }

    fun getCurrentChatFlag() : Int {
        return chatOutputHandler.currentFlag
    }

    fun getCurrentChatFlagSet() : Int {
        return chatOutputHandler.currentFlagSet
    }

    fun getFollowersOnlyTime() : Int {
        return chatOutputHandler.followersOnlyTime
    }

    fun getSlowChatTime() : Int {
        return chatOutputHandler.slowChatTime
    }


    override fun clear() {
        super.clear()
        chatOutputHandler.clear()
    }

}