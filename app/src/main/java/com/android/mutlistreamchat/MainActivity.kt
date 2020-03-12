package com.android.mutlistreamchat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.multistreamchat.Chat
import com.android.multistreamchat.ChatParser
import com.android.multistreamchat.DataListener
import com.android.multistreamchat.TwitchChatParser
import com.android.mutlistreamchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        chatAdapter = ChatAdapter()
        binding.list.adapter = chatAdapter
        Chat.Builder()
            .autoConnect("nickeh30")
            .setClient(Chat.HOST, Chat.PORT)
            .setUserToken("7uyg0kooxcagt096sig5f2i023mrdk")
            .setUsername("is0xxx")
            .setChatParser(TwitchChatParser::class.java)
            .addDataListener(object : DataListener {
                override fun onReceive(message: ChatParser.Message) {
                    chatAdapter.addLine(message)
                }
            })
            .build()
    }
}
