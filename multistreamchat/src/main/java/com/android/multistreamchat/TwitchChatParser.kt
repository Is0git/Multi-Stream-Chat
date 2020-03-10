package com.android.multistreamchat

open class TwitchChatParser : ChatParser() {
    //    """/w+=w*;"""
    companion object {
        const val userMessageRegexPattern = "([\\w-#]+=[\\w-#/]+)"
    }


    var userMessageRegex = userMessageRegexPattern.toRegex()
    override fun parseUserMessage(message: String): Map<String, String> {
        val splitted = message.split('!', '@', '#', ':', limit = 6)
        val res = userMessageRegex.findAll(message, 1).toMap {
            var equalsPosition: Int = 0
            for (a in 0 until it.count()) {
                if (it[a] == '=') {
                    equalsPosition = a
                    break
                }
            }
            Pair(it.slice(0 until equalsPosition), it.slice(equalsPosition + 1 until it.count()))
        }
        return res
    }

    override fun unknownMessage(message: String) {

    }

    override fun lineType(message: String) {

    }

}

fun Sequence<MatchResult>.toMap(split: (value: String) -> Pair<String, String>): Map<String, String> {
    val map = mutableMapOf<String, String>()
    this.forEach {
        split(it.value).also { pair ->
            map[pair.first] = pair.second
        }
    }
    return map
}