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
import com.android.multistreamchat.chat_emotes.TwitchEmotesManager
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
            .addEmoteStateListener(object : EmoteStateListener<Int, TwitchEmotesManager.TwitchEmote> {
                override fun onStartFetch() {

                }

                override fun onEmotesFetched() {

                }

                override fun onDownload() {

                }

                override fun onFailed(throwable: Throwable?) {

                }

                override fun onComplete(emoteSet: List<TwitchEmotesManager.TwitchEmote>) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        emoteAdapter.twitchEmotesList = emoteSet
                        binding.progressBar.visibility = View.INVISIBLE
                    }
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
