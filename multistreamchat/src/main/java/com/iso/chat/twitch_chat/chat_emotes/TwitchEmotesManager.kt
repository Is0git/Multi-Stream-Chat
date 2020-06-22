package com.iso.chat.twitch_chat.chat_emotes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.set
import com.bumptech.glide.Glide
import com.iso.chat.ScreenUnit
import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.chat.chat.listeners.EmoteStateListener
import com.iso.chat.twitch_chat.api.RetrofitInstance
import com.iso.chat.twitch_chat.api.twitch.helpers.getEmoteImageUrlString
import com.iso.chat.twitch_chat.api.twitch.services.EmotesService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
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

    @ExperimentalCoroutinesApi
    override fun getGlobalEmotes() {
        emotesDownloadJob = CoroutineScope(Dispatchers.IO).launch {
            emoteService.getGlobalEmotes(0).also { response ->
                withContext(Dispatchers.Main) {
                    emoteStateListener?.forEach {
                        it.onStartFetch()
                    }
                }
                if (!response.isSuccessful) throw CancellationException("failed request")
                val emoticons =
                    response.body()?.emoticon_sets?.values?.first() ?: throw CancellationException(
                        "Null"
                    )
                val channel = produce(capacity = Channel.BUFFERED) {
                    send(emoticons)
                }
                mapEmotes(channel)
                withContext(Dispatchers.Main) {
                    emoteStateListener?.forEach { it.onEmotesFetched(emoticons) }
                    emoteStateListener?.forEach { it.onComplete() }
                }
            }
        }
    }

    private suspend fun mapEmotes(channel: ReceiveChannel<List<TwitchEmote>>) {
        coroutineScope {
            repeat(10) {
                launch {
                    for (twitchEmote in channel) {
                        twitchEmote.forEach {
                            it.emoteBitMap = getEmoteBitmap(it)
                        }
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

    @SuppressLint("NewApi")
    override suspend fun createEmoteSpannable(
        message: String,
        emotes: Array<TwitchEmote?>?,
        spannable: SpannableStringBuilder
    ): Spannable {
        if (emotes.isNullOrEmpty()) return spannable.append(message)
        message.splitToSequence(" ").also {
            it.forEach { word ->
                for (i in emotes.indices) {
                    if (emotes[i]?.code == word) {
                        spannable.append(
                            word,
                            ImageSpan(
                                context,
                                emotes[i]?.emoteBitMap ?: getEmoteBitmap(emotes[i]!!)
                            ),
                            0
                        )
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
//badges=premium/1;client-nonce=4554af312f9c4fec2279c46ed5cf7875
    override fun getEmotesPositionPairs(emotesRaw: String?): Map<String, List<Pair<Int, Int>>> {
        val map = mutableMapOf<String, List<Pair<Int, Int>>>()
        emotesRaw?.split('/')?.forEach { idPositionsSplit ->
            idPositionsSplit.split(':').also { it ->
                val id = it[0]
                if (it.count() > 1) {
                    val rawPairs = it[1].split(',')
                    val pairs = rawPairs.map { pair ->
                        val pairSplit = pair.split('-')
                        Pair(pairSplit.first().toInt(), pairSplit[1].toInt())
                    }
                    map[id] = pairs
                }
            }
        }
        //25:0-4,12-16/1902:6-10
        return map
    }

    override suspend fun getEmoteBitmap(emote: TwitchEmote): Bitmap {
        return withContext(Dispatchers.IO) {
            val drawable = fetchEmoteDrawable(emote.imageUrl)
            emote.emoteDrawable = drawable
            return@withContext drawable.convertDrawableToEmoteBitmap(context, 25, 28)
        }
    }

    private fun fetchEmoteDrawable(imageUrl: String?): Drawable {
        return Glide.with(context).load(imageUrl).submit().get()
    }



    override suspend fun appendEmotes(
        pairs: Map<String, List<Pair<Int, Int>>>?,
        spannable: SpannableStringBuilder
    ): Spannable? {
        if (pairs == null || pairs.count() == 0) return null
        return coroutineScope {
            for ((emoteId, pairs) in pairs) {
                var bitmap = cachedEmotes[emoteId]
                if (bitmap == null) {
                    bitmap = fetchDrawable(getEmoteImageUrlString(emoteId), context).convertDrawableToEmoteBitmap(context, 25, 28)
                    cachedEmotes[emoteId] = bitmap
                }
                pairs.onEach {
                    launch(Dispatchers.Default) {
                        spannable[it.first..it.second + 1] = ImageSpan(context, bitmap, 0)
                    }
                }
            }
            spannable
        }
    }
}

 fun fetchDrawable(imageUrl: String?, context: Context): Drawable {
    return Glide.with(context).load(imageUrl).submit().get()
}

fun Drawable.convertDrawableToEmoteBitmap(context: Context, width: Int, height: Int): Bitmap {
    return this.toBitmap(
        ScreenUnit.convertDpToPixel(
            width,
            context
        ), ScreenUnit.convertDpToPixel(height, context)
    )
}
