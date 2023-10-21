package com.example.campusconnect

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnect.databinding.ItemSkillsBinding

class SkillsAdapter(private val context: Context, private val list: List<String>?) :
    RecyclerView.Adapter<SkillsAdapter.ViewHolder>() {
    inner class ViewHolder(binding: ItemSkillsBinding) : RecyclerView.ViewHolder(binding.root) {
        val skills = binding.skill

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemSkillsBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val skillsModel = list?.get(position)
        Log.d("dubeyji", "onBindViewHolder: $skillsModel")
        holder.skills.text = skillsModel
    }

}