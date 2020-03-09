package com.android.multistreamchat

abstract class ChatParser {

    abstract fun parseUserMessage(message: String)

    abstract fun unknownMessage(message: String)

}