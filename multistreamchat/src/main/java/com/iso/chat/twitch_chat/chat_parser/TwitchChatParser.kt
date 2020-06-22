package com.iso.chat.twitch_chat.chat_parser

import com.iso.chat.chat.badges.BadgesManager
import com.iso.chat.chat.chat_parser.ChatParser

open class TwitchChatParser : ChatParser() {
    //    """/w+=w*;"""
    companion object {
        const val privMsgPattern = "(PRIVMSG #[\\w]*\\s:)"
    }

    override fun mapMessage(message: String): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        message.split(";").forEach {
            val s = it.split("=")
            map[s.first()] = s[1]
        }
        val privMessage = map["user-type"]?.split(':')
        map["message"] = privMessage?.get(2)
        return map
    }

    //badges=moderator/1,subscriber/12,bits/100
    override fun parseBadgesFromMessage(rawMessage: String?): List<BadgesManager.RawBadge>? {
        if (rawMessage == null) return null
        return rawMessage.split(',').map {
            val split = it.split('/').foldIndexed(BadgesManager.RawBadge()) { index, acc, s ->
                if (index == 0) acc.code = s else acc.version = s.toInt()
                acc
            }
          split
        }
    }

    override fun unknownMessage(message: String) {

    }



    fun parseMessage(rawMessage: String?, channelName: String, commandName: String) : String? {
      return  rawMessage?.substringAfter("$commandName #$channelName :")
    }

    override fun lineType(message: String) {

    }
}

fun Sequence<MatchResult>.toMap(split: (value: String) -> Pair<String, String>): MutableMap<String, String> {
    val map = mutableMapOf<String, String>()
    this.forEach {
        split(it.value).also { pair ->
            map[pair.first] = pair.second
        }
    }
    return map
}