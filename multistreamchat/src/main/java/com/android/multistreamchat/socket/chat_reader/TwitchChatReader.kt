package com.android.multistreamchat.socket.chat_reader

import android.util.Log
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class TwitchChatReader(host: String, port: Int) : ChatReader(host, port) {
    override fun disconnect(writer: Writer?) {

    }

    override fun onConnected(writer: BufferedWriter?, reader: BufferedReader?, user: User, channelName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val channel = Channel<ChatParser.Message>(Channel.CONFLATED)
            try {
                writer?.apply {
                    if (user.token != null) write("PASS oauth:${user.token}\n")
                    write("NICK ${user.name}\n")
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
                                    handleUserMessage(channel, it)
                                }
                                else -> return@let
                            }
                        }
                    }
                }

                for (message in channel) {
                    withContext(Dispatchers.Main) {
                        dataListeners.forEach {
                            it.onReceive(message)
                        }
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


    override fun onFailed(message: String) {

    }
}