package com.iso.chat.chat.listeners

import android.text.Spannable

interface DataListener {
    fun onReceive(message: Spannable)
}