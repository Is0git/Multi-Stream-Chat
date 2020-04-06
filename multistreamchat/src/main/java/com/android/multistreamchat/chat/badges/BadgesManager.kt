package com.android.multistreamchat.chat.badges

import kotlinx.coroutines.*

abstract class BadgesManager<T> {

    var channelBadges: Map<String, T>? = null

    var allBadgesFetchJob: Job? = null

    abstract suspend fun fetchBadges(channelName: String): Map<String, T>

     fun getAllBadges(channelName: String) {
        allBadgesFetchJob =  CoroutineScope(Dispatchers.IO).launch {
            fetchBadges(channelName).also { channelBadges = it }
        }
    }

    fun getMessageBadges(badgesCodes: List<String>?): List<String>? {
        val badges: MutableList<String>
        if (badgesCodes.isNullOrEmpty() && allBadgesFetchJob == null && !allBadgesFetchJob?.isCompleted!!) throw CancellationException("message has no badges")
        else {
            badges = mutableListOf()
            badgesCodes?.forEach {
                channelBadges?.get(it)?.also { badge ->
                    addBadge(badge, badges)
                }
            }
        }
        return badges
    }

    abstract fun addBadge(badge: T, list: MutableList<String>?)

    fun clear() {
        allBadgesFetchJob?.cancel().also { allBadgesFetchJob = null }
    }
}