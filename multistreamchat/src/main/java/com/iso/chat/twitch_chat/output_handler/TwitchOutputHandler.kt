package com.iso.chat.twitch_chat.output_handler

import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.text.toSpannable
import com.iso.chat.chat.Chat
import com.iso.chat.chat.badges.BadgesManager
import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.chat.chat.chat_output_handler.ChatOutputHandler
import com.iso.chat.chat.chat_parser.ChatParser
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.iso.chat.twitch_chat.chat_parser.TwitchChatParser
import kotlinx.coroutines.*

class TwitchOutputHandler(
    chatParser: ChatParser,
    emotesManager: EmotesManager<*, *>,
    badgesManager: BadgesManager<*>
) : ChatOutputHandler(chatParser, emotesManager, badgesManager) {

    var onRoomStateListener: OnRoomStateListener? = null

    override suspend fun handleUserMessage(rawMessage: String) {
        coroutineScope {
            chatParser.mapMessage(rawMessage).also { parsedIrcMessage ->
                val spannable = SpannableStringBuilder()
                val getEmotePositionPairs = supervisorScope {
                    async(Dispatchers.Default) {
                        try {
                            emotesManager.getEmotesPositionPairs(parsedIrcMessage["emotes"])
                        } catch (ex: Exception) {
                            Log.d(Chat.TAG, "exception while getting message emotes: $ex")
                            null
                        }
                    }
                }
                val badgesCodes =
                    async(Dispatchers.Default) { chatParser.parseBadgesFromMessage(parsedIrcMessage["badges"]) }
                var usernameColorHex = parsedIrcMessage["color"]
                if (usernameColorHex.isNullOrBlank()) usernameColorHex = "#868686"
                val message = parsedIrcMessage["message"] ?: ""
                val displayName = parsedIrcMessage["display-name"] ?: ""
                badgesManager.addBadgesToSpannable(badgesCodes.await(), spannable)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    spannable.append(
                        "$displayName: ",
                        ForegroundColorSpan(Color.parseColor(usernameColorHex)),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    withContext(Dispatchers.Default) {
                        spannable.append("$displayName: ", spannable.length, displayName.count())
                    }
                }
                val spannableMessage = (emotesManager as TwitchEmotesManager).appendEmotes(
                    getEmotePositionPairs.await(),
                    SpannableStringBuilder(message)
                )
                withContext(Dispatchers.Default) {
                    if (spannableMessage != null) spannable.append(
                        spannableMessage
                    ) else spannable.append(message)
                }
                withContext(Dispatchers.Main) {
                    dataListeners?.forEach { it.onReceive(spannable) }
                }
            }
        }
    }

    override suspend fun handleRoomStateChange(rawMessage: String) {
        NONE
        chatParser.mapMessage(rawMessage.trimStart('@').substringBefore(" :")).apply {
            get("emote-only")?.toInt()?.apply {
                if (this == 1) currentFlagSet enable ONLY_EMOTES_MODE else currentFlagSet disable ONLY_EMOTES_MODE
            }
            get("followers-only")?.toInt()?.apply {
                followersOnlyTime = if (this >= 0) {
                    currentFlagSet enable FOLLOWERS_ONLY
                    this
                } else {
                    currentFlagSet disable FOLLOWERS_ONLY
                    0
                }
            }
            get("r9k")?.toInt()?.apply {
                if (this == 1) currentFlagSet enable R9K_MODE else currentFlagSet disable R9K_MODE
            }
            get("slow")?.toInt()?.apply {
                slowChatTime = if (this >= 3) {
                    currentFlagSet enable SLOW_MODE
                    this
                } else {
                    currentFlagSet disable SLOW_MODE
                    0
                }
            }
            get("subs-only")?.toInt()?.apply {
                if (this == 1) currentFlagSet enable SUB_MODE else currentFlagSet disable SUB_MODE
            }
            currentFlag = findLastEnabledFlag(currentFlagSet)
            withContext(Dispatchers.Main) {
                onRoomStateListener?.onRoomStateChanged(
                    currentFlagSet,
                    currentFlag
                )
            }
        }
    }

    override suspend fun handleServerMessage(rawMessage: String, channelName: String) {
        val message =
            (chatParser as TwitchChatParser).parseMessage(rawMessage, channelName, "NOTICE")
        if (message.isNullOrEmpty()) throw CancellationException("notice message is empty or null")
        val spannable = message.toSpannable()
        withContext(Dispatchers.Main) {
            dataListeners?.forEach {
                it.onReceive(spannable)
            }
        }
    }


    private fun findFirstEnabledFlag(flagSet: Int) : Int {
        return when {
            flagSet hasEnabled FOLLOWERS_ONLY -> FOLLOWERS_ONLY
            flagSet hasEnabled R9K_MODE -> R9K_MODE
            flagSet hasEnabled SLOW_MODE -> SLOW_MODE
            flagSet hasEnabled SUB_MODE -> SUB_MODE
            flagSet hasEnabled ONLY_EMOTES_MODE -> ONLY_EMOTES_MODE
            else -> NONE
        }
    }

    private fun findLastEnabledFlag(flagSet: Int) : Int {
        return when {
            flagSet hasEnabled ONLY_EMOTES_MODE -> ONLY_EMOTES_MODE
            flagSet hasEnabled SUB_MODE -> SUB_MODE
            flagSet hasEnabled SLOW_MODE -> SLOW_MODE
            flagSet hasEnabled R9K_MODE -> R9K_MODE
            flagSet hasEnabled FOLLOWERS_ONLY -> FOLLOWERS_ONLY
            else -> NONE
        }
    }

    private infix fun Int.hasEnabled(flag: Int) : Boolean{
        return (this and flag) > 0
    }
    private infix fun Int.enable(flag: Int) {
        currentFlagSet = currentFlagSet or flag
    }

    private infix fun Int.disable(flag: Int) {
        currentFlagSet = flag.inv().and(this)
    }
}



interface OnRoomStateListener {
    fun onRoomStateChanged(roomStateFlags: Int, firstEnabledFlag: Int)
}