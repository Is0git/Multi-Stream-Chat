package com.android.mutlistreamchat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.mutlistreamchat.databinding.ChatLayoutBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    var linesList: MutableList<ChatParser.Message> = mutableListOf()

    class MyViewHolder(val binding: ChatLayoutBinding) : RecyclerView.ViewHolder(binding.root){

    }

    fun addLine(line: ChatParser.Message?) {
        line?.let {
            if (linesList.count() > 50) linesList.clear().also { notifyDataSetChanged() }
            linesList.add(line)
            notifyItemChanged(linesList.count() - 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int  = linesList.count()

    @ExperimentalCoroutinesApi
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.user.apply {
           text = linesList[position].username
            setTextColor(Color.parseColor(linesList[position].usernameColor))
        }
        holder.binding.text.text = if (linesList[position].spannnableMessage.isNullOrEmpty()) linesList[position].message else linesList[position].spannnableMessage


//        holder.binding.text.setTextColor(Color.parseColor(linesList[position].usernameColor))
    }


}