package com.android.mutlistreamchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.multistreamchat.chat_emotes.TwitchEmoteManager
import com.android.mutlistreamchat.databinding.EmoteItemLayoutBinding

class EmoteAdapter : RecyclerView.Adapter<EmoteAdapter.MyViewHolder>() {

    var twitchEmotesList: List<TwitchEmoteManager.TwitchEmote>? = null
    set(value) {
        field= value
        notifyDataSetChanged()
    }

    class MyViewHolder(val emoteItemLayoutBinding: EmoteItemLayoutBinding) : RecyclerView.ViewHolder(emoteItemLayoutBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val binding = EmoteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return twitchEmotesList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.emoteItemLayoutBinding.image.setImageDrawable(twitchEmotesList?.get(position)?.imageDrawable)
    }
}