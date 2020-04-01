package com.android.multistreamchat.chat.chat_parser

import android.text.Spannable

abstract class ChatParser {

    abstract fun parseUserMessage(message: String): Map<String, String>

    abstract fun parseBadgesFromMessage(rawMessage: String?) : List<String>?

    abstract fun unknownMessage(message: String)

    abstract fun lineType(message: String)

    data class Message(var username: String, val message: String, val channel: String, var usernameColor: String, var spannableMessage: Spannable?, var badges: List<String>?)

}