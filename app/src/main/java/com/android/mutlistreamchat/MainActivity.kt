package com.android.mutlistreamchat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.multistreamchat.Chat
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.DataListener
import com.android.multistreamchat.chat_parser.TwitchChatParser
import com.android.mutlistreamchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val chat =Chat.Builder()
            .autoConnect("is0xxx")
            .setClient(Chat.HOST, Chat.PORT)
            .setUserToken("7uyg0kooxcagt096sig5f2i023mrdk")
            .setUsername("is0xxx")
            .setChatParser(TwitchChatParser::class.java)
            .addDataListener(object : DataListener {
                override fun onReceive(message: ChatParser.Message) {
                    chatAdapter.addLine(message)
                }
            })
            .build(this)

        chatAdapter = ChatAdapter(chat)
        binding.list.adapter = chatAdapter

        binding.button.setOnClickListener {
            Toast.makeText(applicationContext, "DRAW: ${chat.getEmoteById(25)}", Toast.LENGTH_SHORT).show()
        }

    }
}
