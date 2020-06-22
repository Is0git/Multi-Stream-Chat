package com.iso.chat.chat.socket.chat_reader

import android.util.Log
import com.iso.chat.chat.Chat.Companion.TAG
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler
import com.iso.chat.chat.user.User
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Writer
import kotlin.coroutines.CoroutineContext

class TwitchChatReader(
    host: String,
    port: Int,
    user: User?,
    outputHandler: ChatOutputHandler,
    channelName: String?
) : ChatReader(host, port, user, outputHandler, channelName) {

    private val parseJobHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, "${throwable.message}", throwable)
    }
    private val roomStateChangeHandler = CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable ->
        Log.d(TAG, "room state change ex: $throwable")
    }
    var roomStateJob: Job? = null

    override fun disconnect(writer: Writer?) {

    }

    override fun clear() {
        super.clear()
        channel?.close()
        roomStateJob?.cancel()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun onConnected(
        writer: BufferedWriter?,
        reader: BufferedReader?,
        user: User?,
        channelName: String
    ) {
        channel = Channel<String?>(1)
        coroutineScope {
            writer?.apply {
                if (!user?.token.isNullOrBlank()) write("PASS oauth:${user?.token}\n")
                write("NICK ${user?.name}\n")
                write("CAP REQ :twitch.tv/tags\n")
                write("CAP REQ :twitch.tv/commands\n")
                write("JOIN #$channelName\n")
                flush()
                launch(Dispatchers.IO) {
                    while (true) {
                        val line = reader?.readLine()
                        (channel as Channel<String?>).send(line)

                    }
                }
                launch(Dispatchers.IO) {
                    for (line in channel!! as Channel<String?>) {
                        Log.d("CHATMES", "$line")
                        Log.d("CHANNEL_TEST", "received: $line currentCoroutine: ${this.coroutineContext}")
                        line?.let {
                            when {
                                it.contains("PRIVMSG #$channelName") -> {
                                    supervisorScope {
                                        launch(parseJobHandler) { handleUserMessage(it) }
                                    }
                                }
                                it.contains("Welcome, GLHF!") -> {
                                    withContext(Dispatchers.Main) {
                                        chatConnectivityListener?.onConnectivityStateChange(
                                            true,
                                            "Twitch"
                                        )
                                    }
                                }
                                it.contains("ROOMSTATE #$channelName") -> roomStateJob = CoroutineScope(roomStateChangeHandler).launch {
                                        handleRoomStateChange(it)
                                }
                                it.contains(" NOTICE #$channelName") -> supervisorScope {
                                    launch(parseJobHandler) {
                                        handleServerMessage(it, channelName)
                                    }
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


suspend fun BufferedReader.readLineSuspending(): String? =
    withContext(Dispatchers.IO) { readLine() }


suspend fun BufferedWriter.writeLineSuspending(line: String) =
    withContext(Dispatchers.IO) { write(line) }

suspend fun BufferedWriter.flushSuspending() =
    withContext(Dispatchers.IO) { flush() }