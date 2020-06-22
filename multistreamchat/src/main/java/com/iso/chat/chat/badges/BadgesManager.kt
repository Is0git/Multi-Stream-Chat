package com.iso.chat.chat.badges

import android.text.SpannableStringBuilder
import android.util.Log
import com.iso.chat.chat.Chat
import com.iso.chat.twitch_chat.api.twitch.models.badges.unofficial_badges.Version
import kotlinx.coroutines.*

abstract class BadgesManager<T> {

    var channelBadges: MutableMap<String, T> = mutableMapOf()
    var globalBadgesJob: Job? = null
    var channelBadgesJob: Job? = null
    var badgeJobHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d(Chat.TAG, "badgesJob caught exception : $throwable")
    }

     fun getAllBadges(channelName: String) {
        globalBadgesJob = CoroutineScope(Dispatchers.IO + badgeJobHandler).launch {
            fetchGlobalBadges().also { channelBadges.putAll(it) }
            channelBadgesJob = launch {
                val channelId = withContext(Dispatchers.IO) { getChannelId(channelName) }
                fetchChannelBadges(channelId).also {
                    (channelBadges["subscriber"] as Version).versions.putAll((it["subscriber"] as Version).versions)
                    channelBadges.putAll(it)
                }
            }
            channelBadgesJob?.invokeOnCompletion {
                if (it != null) Log.e(
                    Chat.TAG,
                    "failed to load channel badges"
                ) else Log.i(Chat.TAG, "channels badges job is completed successfully")
            }
        }
        globalBadgesJob?.invokeOnCompletion { cause: Throwable? ->
            if (cause != null) Log.e(Chat.TAG, "failed to load global badges") else Log.i(
                Chat.TAG,
                "global badges job is completed successfully"
            )
        }
    }

    fun getMessageBadges(badgesCodes: List<String>?): List<String>? {
        val badges: MutableList<String>
        if (badgesCodes.isNullOrEmpty() && globalBadgesJob == null && !globalBadgesJob?.isCompleted!!) return null
        else {
            badges = mutableListOf()
            badgesCodes?.forEach {
                channelBadges[it]?.also { badge ->
                    addBadge(badge, badges)
                }
            }
        }
        return badges
    }

    fun clear() {
        globalBadgesJob?.cancel()
    }

    abstract suspend fun getChannelId(channelName: String): String
    abstract suspend fun fetchGlobalBadges(): Map<String, T>
    abstract suspend fun addBadgesToSpannable(
        rawBadges: List<RawBadge>?,
        spannableStringBuilder: SpannableStringBuilder
    ): SpannableStringBuilder

    abstract fun addBadge(badge: T, list: MutableList<String>?)
    abstract suspend fun fetchChannelBadges(channelId: String): Map<String, T>

    data class RawBadge(var code: String? = null, var version: Int? = null)
}