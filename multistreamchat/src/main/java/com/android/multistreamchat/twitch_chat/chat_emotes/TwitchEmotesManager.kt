package com.android.multistreamchat.twitch_chat.chat_emotes

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.core.graphics.drawable.toBitmap
import com.android.multistreamchat.chat.chat_emotes.EmotesManager
import com.android.multistreamchat.chat.listeners.EmoteStateListener
import com.android.multistreamchat.twitch_chat.api.RetrofitInstance
import com.android.multistreamchat.twitch_chat.api.twitch.services.EmotesService
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce


class TwitchEmotesManager(
    private val context: Context,
    private val emoteStateListener: List<EmoteStateListener<String, TwitchEmote>>? = null
) :
    EmotesManager<String, TwitchEmotesManager.TwitchEmote>(emoteStateListener) {

    companion object {
        const val emotesIdsPattern = "([\\d]+:)"
    }

    var emotesIdsRegex = emotesIdsPattern.toRegex()

    private var emoteService: EmotesService =
        RetrofitInstance.getRetrofit("https://api.twitch.tv").create(EmotesService::class.java)

    init {
        getGlobalEmotes()
        emotesDownloadJob?.invokeOnCompletion { throwable ->
            throwable?.let { emoteStateListener?.forEach { listener -> listener.onFailed(it) } }
        }
    }

    override fun getGlobalEmotes() {
        emotesDownloadJob = CoroutineScope(Dispatchers.IO).launch {

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
                    response.body()?.emoticon_sets?.forEach { set ->
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

    override suspend fun getEmote(id: Int): TwitchEmote {
        return emoteService.getEmote(id = id).let { response ->
            if (response.isSuccessful && !response.body().isNullOrEmpty()) response.body()?.first()!! else throw CancellationException(
                "couldn't get an emote: ${response.message()}"
            )
        }

    }

    override suspend fun createEmoteSpannable(message: String, emotes: Array<TwitchEmote?>) : Spannable {
        val spannable = SpannableStringBuilder()
        message.splitToSequence(" ").also {
            it.forEach { word ->
                for (i in emotes.indices) {
                    if (emotes[i]?.code == word) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            spannable.append(
                                word,
                                ImageSpan(
                                    context,
                                    getEmoteDrawable(emotes[i]!!).toBitmap(50, 50)
                                ),
                                0
                            )
                        }
                        break
                    } else if (i == emotes.count() - 1) spannable.append("$word ")
                }
            }
        }
        return spannable
    }

    class TwitchEmote(val id: Int?) : Emote() {
        constructor(id: Int, imageUrl: String, code: String?) : this(id) {
            this.code = code
            this.imageUrl = imageUrl
        }
    }

    override fun extractEmoteIds(emotesRaw: String?): Array<String>? {
        var array: Array<String>?
        if (emotesRaw.isNullOrEmpty()) return null
        emotesIdsRegex.findAll(emotesRaw).also {
            if (it.count() == 0) return null
            array = Array(it.count()) { "" }
            it.forEachIndexed { index: Int, matchResult: MatchResult ->
                array!![index] = matchResult.value.dropLast(1)
            }
        }
        return array
    }

    override suspend fun getMessageEmoteCodes(emotesIds: Array<String>): Array<TwitchEmote?> {
        val emoteCodeArray: Array<TwitchEmote?> = Array(emotesIds.size) { null }
        emotesIds.forEachIndexed { index, i ->
            emoteCodeArray[index] = getEmote(i.toInt())
        }
        return emoteCodeArray
    }

    override suspend fun getEmoteDrawable(emote: TwitchEmote): Drawable {
        return withContext(Dispatchers.IO) {
            Glide.with(context).load(emote.imageUrl).submit().get()
        }
    }


}

