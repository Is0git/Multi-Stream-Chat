package com.android.mutlistreamchat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.multistreamchat.Chat
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.DataListener
import com.android.multistreamchat.chat_emotes.EmoteStateListener
import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_emotes.TwitchEmoteManager
import com.android.multistreamchat.chat_parser.TwitchChatParser
import com.android.mutlistreamchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
    lateinit var emoteAdapter: EmoteAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        emoteAdapter = EmoteAdapter()
        val chat = Chat.Builder()
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
            .addEmoteStateListener(object : EmoteStateListener<Int> {
                override fun onDownloaded(emote: Map<Int, EmotesManager.Emote>) {
                    emoteAdapter.twitchEmotesList = (emote.toList().map { it.second }) as List<TwitchEmoteManager.TwitchEmote>
                }


            })
            .build(this)

        chatAdapter = ChatAdapter()
        binding.list.adapter = chatAdapter

        binding.button.setOnClickListener {
            chat.typeMessage("saasdsd")
        }

    }
}
