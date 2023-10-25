package com.example.campusconnect.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusconnect.R
import com.example.campusconnect.databinding.ChatlistitemBinding
import com.example.campusconnect.model.model
import com.example.campusconnect.ui.ChatActivity

class ChatListAdapter(private val context: Context,private var list: List<model>):RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    inner  class ViewHolder(binding: ChatlistitemBinding):RecyclerView.ViewHolder(binding.root){

        val profileImage=binding.profileImage
        val name=binding.name

    }
    fun setFilteredList(list: ArrayList<model>){
        this.list=list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {

        return ViewHolder(
            ChatlistitemBinding.inflate(LayoutInflater.from(context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
       val model=list[position]
        holder.name.text=model.name
        Glide.with(context).load(model.profileImage).placeholder(R.drawable.placeholder).into(holder.profileImage)


        holder.itemView.setOnClickListener {
            val intent= Intent(context, ChatActivity::class.java)
            intent.putExtra("name",model.name)
            intent.putExtra("image",model.profileImage)
            intent.putExtra("uid",model.uid)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}