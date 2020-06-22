package com.iso.chat.twitch_chat.badges

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import com.iso.chat.chat.badges.BadgesManager
import com.iso.chat.twitch_chat.api.RetrofitInstance
import com.iso.chat.twitch_chat.api.twitch.models.badges.unofficial_badges.Version
import com.iso.chat.twitch_chat.api.twitch.services.BadgeService
import com.iso.chat.twitch_chat.chat_emotes.convertDrawableToEmoteBitmap
import com.iso.chat.twitch_chat.chat_emotes.fetchDrawable
import kotlinx.coroutines.*

class TwitchBadgesManager(var context: Context) :
    BadgesManager<Version>() {
    private var badgeService: BadgeService =
        RetrofitInstance.getRetrofit("https://api.twitch.tv").create(BadgeService::class.java)

    override suspend fun fetchGlobalBadges(): Map<String, Version> {
        return withContext(Dispatchers.IO) {
            val result = badgeService.getGlobalBadges()
            if (!result.isSuccessful || result.body()?.badgesSets.isNullOrEmpty()) throw CancellationException(
                "no users"
            )
            return@withContext result.body()?.badgesSets!!
        }
    }

    override fun addBadge(badge: Version, list: MutableList<String>?) {

    }

    override suspend fun addBadgesToSpannable(
        rawBadges: List<RawBadge>?,
        spannableStringBuilder: SpannableStringBuilder
    ): SpannableStringBuilder {
        if (rawBadges.isNullOrEmpty() || channelBadges.isEmpty()) return spannableStringBuilder
        coroutineScope {
            rawBadges.forEach {
                val badge = channelBadges[it.code]?.versions?.get(it.version!!.toString()) ?: return@forEach
                launch(Dispatchers.Default) {
                    if (badge.badgeBitmap == null) {
                        badge.badgeBitmap =
                            fetchDrawable(badge.imageUrl2x, context).convertDrawableToEmoteBitmap(
                                context,
                                18,
                                18
                            )

                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        spannableStringBuilder.append(
                            it.code,
                            ImageSpan(context, badge.badgeBitmap!!),
                            0
                        )
                    }
                }
            }
        }
        return spannableStringBuilder
    }

    override suspend fun fetchChannelBadges(channelId: String): Map<String, Version> {
        return coroutineScope {
            val badgesResponse = badgeService.getChannelBadges("https://badges.twitch.tv/v1/badges/channels/$channelId/display")
            if (!badgesResponse.isSuccessful || badgesResponse.body() == null || badgesResponse.body()?.badgesSets.isNullOrEmpty()) throw CancellationException(
                "badges null or empty"
            )
            badgesResponse.body()?.badgesSets!!
        }
    }

    override suspend fun getChannelId(channelName: String): String {
        val responseResult = badgeService.getChannel(name = channelName)
        if (!responseResult.isSuccessful || responseResult.body()?.users.isNullOrEmpty()) throw CancellationException(
            "null id"
        )
        return responseResult.body()?.users?.first()?._id!!
    }
}