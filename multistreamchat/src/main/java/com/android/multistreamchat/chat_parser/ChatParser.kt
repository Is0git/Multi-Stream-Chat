package com.android.multistreamchat.chat_parser

import android.text.Spannable
import android.text.SpannableString

abstract class ChatParser {

    abstract fun parseUserMessage(message: String): Map<String, String>

    abstract fun extractEmoteIds(emotesRaw: String?) : Array<Int>?

    abstract fun unknownMessage(message: String)

    abstract fun lineType(message: String)

    data class Message(var username: String, val message: String, val channel: String, var usernameColor: String, var spannnableMessage: Spannable)

}