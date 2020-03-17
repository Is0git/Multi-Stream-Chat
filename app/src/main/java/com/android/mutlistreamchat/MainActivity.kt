package com.android.mutlistreamchat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.multistreamchat.Chat
import com.android.multistreamchat.chat_parser.ChatParser
import com.android.multistreamchat.DataListener
import com.android.multistreamchat.chat_emotes.EmoteStateListener
import com.android.multistreamchat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat_emotes.TwitchEmoteManager
import com.android.multistreamchat.chat_parser.TwitchChatParser
import com.android.mutlistreamchat.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var chatAdapter: ChatAdapter
    lateinit var emoteAdapter: EmoteAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        emoteAdapter = EmoteAdapter()
        binding.emotesList.adapter = emoteAdapter
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
            .addEmoteStateListener(object : EmoteStateListener<Int, TwitchEmoteManager.TwitchEmote> {
                override fun onDownloaded(emote: Map<Int, TwitchEmoteManager.TwitchEmote>) {
                   lifecycleScope.launch(Dispatchers.Main) {
                       emoteAdapter.twitchEmotesList = emote.toList().map { it.second }
                       binding.progressBar.visibility = View.INVISIBLE
                   }
                }

                override fun onStartLoading() {
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onDownloading() {

                }

                override fun onFailed(e: Throwable) {

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
