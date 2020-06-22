package com.iso.chat.chat.chat_parser

import android.text.Spannable
import com.iso.chat.chat.badges.BadgesManager

abstract class ChatParser {
    abstract fun mapMessage(message: String): Map<String, String?>
    abstract fun parseBadgesFromMessage(rawMessage: String?) : List<BadgesManager.RawBadge>?
    abstract fun unknownMessage(message: String)
    abstract fun lineType(message: String)
    data class Message(var username: String?, val message: String?, val channel: String?, var usernameColor: String?, var spannableMessage: Spannable?, var badges: List<String>?)
}