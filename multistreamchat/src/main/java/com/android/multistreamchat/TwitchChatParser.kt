package com.android.multistreamchat

open class TwitchChatParser : ChatParser() {
    //    """/w+=w*;"""
    companion object {
        const val userMessageRegexPattern = "([\\w-#]+=[\\w-#/]+)"
        const val privMsgPattern = "(PRIVMSG #[\\w]*\\s:)"
    }

    var userMessageRegex = userMessageRegexPattern.toRegex()
    override fun parseUserMessage(message: String): Map<String, String> {
      val resultMap = userMessageRegex.findAll(message, 1).toMap {
            var equalsPosition = 0
            for (a in 0 until it.count()) {
                if (it[a] == '=') {
                    equalsPosition = a
                    break
                }
            }
            Pair(it.slice(0 until equalsPosition), it.slice(equalsPosition + 1 until it.count()))
        }
        val privMsg = message.split(privMsgPattern.toRegex(), 2)
        resultMap["message"] = privMsg[1]
        return resultMap
    }

    override fun unknownMessage(message: String) {

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