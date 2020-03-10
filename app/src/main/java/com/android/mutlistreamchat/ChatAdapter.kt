package com.android.mutlistreamchat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.android.multistreamchat.ChatParser
import com.android.mutlistreamchat.databinding.ChatLayoutBinding

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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.text.text = linesList[position].run {
            "$username: $message"
        }
        holder.binding.text.setTextColor(Color.parseColor(linesList[position].usernameColor))
    }


}