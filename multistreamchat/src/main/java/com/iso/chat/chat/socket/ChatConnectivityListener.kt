package com.iso.chat.chat.socket

interface ChatConnectivityListener {
    fun onConnectivityStateChange(isConnected: Boolean, platformName: String)

    fun onChatModeChange(isAnonymous: Boolean)
}