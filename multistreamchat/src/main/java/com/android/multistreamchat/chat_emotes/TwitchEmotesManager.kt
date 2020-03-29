package com.android.multistreamchat.chat_emotes

import android.content.Context
import com.android.multistreamchat.api.RetrofitInstance
import com.android.multistreamchat.api.twitch.services.EmotesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TwitchEmotesManager(
    private val context: Context,
    private val emoteStateListener: List<EmoteStateListener<String, TwitchEmote>>? = null
) :
    EmotesManager<String, TwitchEmotesManager.TwitchEmote>(emoteStateListener) {


    private var emoteService: EmotesService =
        RetrofitInstance.getRetrofit("https://api.twitch.tv").create(EmotesService::class.java)

    init {
        getGlobalEmotes()
        emoteDownloaderJob?.invokeOnCompletion { throwable ->
            throwable?.let { emoteStateListener?.forEach { listener -> listener.onFailed(it) } }
        }
    }

    override fun getGlobalEmotes() {
        emoteDownloaderJob = CoroutineScope(Dispatchers.IO).launch {

            emoteService.getGlobalEmotes(0).also { response ->
                withContext(Dispatchers.Main) {
                    emoteStateListener?.forEach {
                        it.onStartFetch()
                    }
                }
                val channel = produce(capacity = Channel.RENDEZVOUS) {
                    emoteStateListener?.forEach {
                        it.onDownload()
                    }
                    response.body()?.emoticonSets?.forEach { set ->
//                            val drawable = Glide.with(context).load(url).submit().get()
                            send(set)
                        }

                    }

                withContext(Dispatchers.Main) {
                    for (twitchEmote in channel) {
                        globalEmotes[twitchEmote.key] = twitchEmote.value
                        emoteStateListener?.forEach { it.onComplete(twitchEmote.value) }
                    }
                }
            }
        }
    }

    override fun createsSpannable(message: String, emotesId: Array<String>?) {
//        if (emotesId.isNullOrEmpty()) return null
//        val emoteCodeArray: Array<TwitchEmote?> = Array(emotesId.size) { null }
//        emotesId.forEachIndexed { index, i ->
//            emoteCodeArray[index] = globalEmotes[i]
//        }
//
//        val spannable = SpannableStringBuilder()
//        message.splitToSequence(" ").also {
//            it.forEach { word ->
//                for (i in emoteCodeArray.indices) {
//                    if (emoteCodeArray[i]?.code == word) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            spannable.append(
//                                word,
//                                ImageSpan(
//                                    context,
//                                    emoteCodeArray[i]?.imageDrawable?.toBitmap(25, 25)!!
//                                ),
//                                0
//                            )
//                        }
//                        break
//                    } else if (i == emoteCodeArray.count() - 1) spannable.append(word)
//                }
//            }
//        }
//        return spannable
    }

    class TwitchEmote(
        val id: Int?,
        val imageUrl: String? = null
    ) : Emote() {
        constructor(id: Int, imageUrl: String, code: String?) : this(
            id,
            imageUrl
        ) {
            this.code = code
        }
    }

}

