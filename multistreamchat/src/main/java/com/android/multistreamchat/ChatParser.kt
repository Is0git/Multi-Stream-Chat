package com.android.multistreamchat

abstract class ChatParser {

    abstract fun parseUserMessage(message: String): Map<String, String>

    abstract fun unknownMessage(message: String)

    abstract fun lineType(message: String)

    data class Message(var username: String, val message: String, val channel: String, var usernameColor: String)

}