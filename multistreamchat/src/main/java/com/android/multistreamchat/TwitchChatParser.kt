package com.android.multistreamchat

open class TwitchChatParser : ChatParser() {


    override fun parseUserMessage(message: String) : List<String> {
       val splitted = message.split('!', '@', '#', ':', limit = 6)
       return splitted
    }

    override fun unknownMessage(message: String) {

    }

    override fun lineType(message: String) {

    }

}