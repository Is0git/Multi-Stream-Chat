package com.android.multistreamchat.chat.socket.chat_reader

import android.util.Log
import com.android.multistreamchat.chat.chat_output_handler.ChatOutputHandler
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamchat.chat.user.User
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Writer
import java.lang.Exception

class TwitchChatReader(
    host: String,
    port: Int,
    user: User?,
    outputHandler: ChatOutputHandler,
    channelName: String?
) : ChatReader(host, port, user, outputHandler, channelName) {
    override fun disconnect(writer: Writer?) {

    }

    override fun onConnected(
        writer: BufferedWriter?,
        reader: BufferedReader?,
        user: User?,
        channelName: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val channel = Channel<ChatParser.Message>(Channel.CONFLATED)
            writer?.apply {
                if (user?.token != null) write("PASS oauth:${user.token}\n")
                write("NICK ${user?.name}\n")
                write("CAP REQ :twitch.tv/tags\n")
                write("CAP REQ :twitch.tv/commands\n")
                write("JOIN #$channelName\n")
                flush()
            }

            launch {
                var line: String? = ""
                while (line != null) {
                    line = reader?.readLine()
                    line?.let {
                        Log.d("LINE", "$it")
                        when {
                            line.contains("privmsg", true) -> {
                                supervisorScope {
                                    try {
                                        handleUserMessage(channel, it)
                                    } catch (ex: Exception) {
                                        ex.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }

            }
                for (message in channel) {
                    withContext(Dispatchers.Main) {
                        dataListeners?.forEach {
                            it.onReceive(message)
                        }
                    }
                }
        }
    }

    override fun onFailed(message: String) {

    }
}