package com.android.mutlistreamchat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.android.mutlistreamchat.databinding.ChatLayoutBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    var linesList: MutableList<String> = mutableListOf()

    class MyViewHolder(val binding: ChatLayoutBinding) : RecyclerView.ViewHolder(binding.root){

    }

    fun addLine(line: String?) {
        line?.let {
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
        holder.binding.text.text = linesList[position]
    }


}