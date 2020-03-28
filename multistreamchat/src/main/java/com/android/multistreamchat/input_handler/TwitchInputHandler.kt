package com.android.multistreamchat.input_handler

import com.android.multistreamchat.socket.chat_writer.WriterReaderHelper
import com.android.multistreamchat.socket.chat_writer.TwitchChatWriter
import com.android.multistreamchat.user.User

class TwitchInputHandler(user: User, host: String, port: Int) : ChatInputHandler(user, host, port, TwitchChatWriter(host, port, WriterReaderHelper()))