package com.android.multistreamchat.twitch_chat.badges

import com.android.multistreamchat.chat.badges.BadgesManager
import com.android.multistreamchat.twitch_chat.api.RetrofitInstance
import com.android.multistreamchat.twitch_chat.api.twitch.models.badges.BadgeItem
import com.android.multistreamchat.twitch_chat.api.twitch.services.BadgeService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TwitchBadgesManager : BadgesManager<BadgeItem>() {
    private var badgeService: BadgeService = RetrofitInstance.getRetrofit("https://api.twitch.tv").create(BadgeService::class.java)
    override suspend fun fetchBadges(channelName: String): Map<String, BadgeItem>{
        return withContext(Dispatchers.IO) {
            val result =  badgeService.getChannel(channelName)
            if (result.body()?.users.isNullOrEmpty()) throw CancellationException("no users")
            val id = result.body()?.users?.first()?._id ?: throw CancellationException("no channel id provided")
            badgeService.getChannelBadges(id.toInt()).run {
                when {
                    isSuccessful && body() != null-> body()!!
                    else -> throw CancellationException(errorBody()?.string())
                }
            }
        }
    }

    override fun addBadge(badge: BadgeItem, list: MutableList<String>?) {
        list?.add(badge.image ?: throw CancellationException("no badge image"))
    }
}