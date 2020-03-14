package com.android.multistreamchat.chat_emotes

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.core.graphics.drawable.toBitmap
import com.android.multistreamchat.api.RetrofitInstance
import com.android.multistreamchat.api.services.EmotesService
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch


class TwitchEmoteManager(val context: Context) :
    EmotesManager<Int, TwitchEmoteManager.TwitchEmote>() {

    private var emoteService: EmotesService =
        RetrofitInstance.getRetrofit("https://api.twitch.tv").create(EmotesService::class.java)

    init {
        getGlobalEmotes()
    }

    override fun getGlobalEmotes() {
        emoteDownloaderJob = CoroutineScope(Dispatchers.IO).launch {
            emoteService.getGlobalEmotes(0).also { response ->

                val channel = produce(capacity = Channel.RENDEZVOUS) {
                    response.body()?.emoticon_sets?.`0`?.forEach {
                        val url = "https://static-cdn.jtvnw.net/emoticons/v1/${it.id}/1.0"
                        val drawable = Glide.with(context).load(url).submit().get()
                        send(TwitchEmote(it.id ?: Int.MAX_VALUE, url, drawable, it.code ?: "null"))
                    }
                }
                for (twitchEmote in channel) {
                    globalEmotes[twitchEmote.id] = twitchEmote
                }
            }
        }
    }


    class TwitchEmote(
        val id: Int,
        val imageUrl: String? = null,
        val imageDrawable: Drawable? = null
    ) : Emote() {
        constructor(id: Int, imageUrl: String, imageDrawable: Drawable, code: String) : this(
            id,
            imageUrl,
            imageDrawable
        ) {
            this.code = code
        }
    }


    override fun createsSpannable(message: String, emotesId: Array<Int>?): Spannable {
        val emoteCodeArray: Array<TwitchEmote?> = Array(emotesId?.size ?: 0) { null }
        emotesId?.forEachIndexed { index, i ->
            emoteCodeArray[index] = globalEmotes[i]
        }

        val spannable = SpannableStringBuilder()
        message.splitToSequence(" ").also {
            it.forEach { word ->
                for (i in emoteCodeArray.indices) {

                    if (emoteCodeArray[i]?.code == word) {
                        spannable.append(
                            word,
                            ImageSpan(context,  emoteCodeArray[i]?.imageDrawable?.toBitmap(25, 25)!!),
                            0
                        )
                        break
                    } else if (i == emoteCodeArray.count() - 1) spannable.append(word)
                }
            }
        }
        return spannable
    }
}

