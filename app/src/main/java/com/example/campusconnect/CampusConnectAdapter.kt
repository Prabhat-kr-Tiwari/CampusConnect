package com.example.campusconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusconnect.databinding.ItemBinding

class CampusConnectAdapter(
    private val context: Context,
    private val list: ArrayList<model>
) : RecyclerView.Adapter<CampusConnectAdapter.ViewHolder>() {

    private var onClickListener: View.OnClickListener? = null

    inner class ViewHolder(binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val profileImage = binding.profileImage
        val name = binding.name
        val coursename = binding.courseName
        val collegename = binding.collegeName
        val about = binding.about
        val githuburl = binding.githubUrl
        val linkdinurl = binding.linkdinUrl

        //        val skills = binding.skill1
        val skills = binding.rvSkills
        val childRecyclerView: RecyclerView = binding.rvSkills
        val expertise = binding.expertiseIn


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]
        Glide.with(context).load(model.profileImage).into(holder.profileImage)
        holder.name.text = model.name
        holder.coursename.text = model.coursename
        holder.collegename.text = model.collegename
        holder.about.text = model.about
        try {
            val skillsArray = model.skills?.split(",")

            val childAdapter = SkillsAdapter(context, skillsArray)
            holder.childRecyclerView.layoutManager =
                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.childRecyclerView.adapter = childAdapter
        } catch (e: Exception) {
            Log.d("PrabhatKUmar", "onBindViewHolder: catch while setting the skills model ")
            e.printStackTrace()
        }



        holder.expertise.text = model.expertise
        // Set GitHub URL
        if (!model.githuburl.isNullOrEmpty()) {
//            val githubText = "GitHub: ${model.githuburl}"
            val githubText = "GitHub"
            holder.githuburl.text = githubText

            holder.githuburl.setOnClickListener {
                val webLink = model.githuburl // Replace with your desired URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
                val context = holder.itemView.context // Get the context from the item view

                context.startActivity(intent)
            }


        } else {
            // Hide the GitHub URL TextView if it's empty
            holder.githuburl.visibility = View.GONE
        }

        //for linkdin
        if (!model.linkdinurl.isNullOrEmpty()) {
//            val githubText = "GitHub: ${model.githuburl}"
            val githubText = "Linkedin"
            holder.linkdinurl.text = githubText

            holder.linkdinurl.setOnClickListener {
                val webLink = model.linkdinurl // Replace with your desired URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
                val context = holder.itemView.context // Get the context from the item view

                context.startActivity(intent)
            }


        } else {
            // Hide the GitHub URL TextView if it's empty
            holder.linkdinurl.visibility = View.GONE
        }


    }

    //step 1 for adding onclicklistener on reclerview
    /* interface OnClickListener {
         fun onClick(position: Int, models: HappyPlaceModels) {

         }
     }*/


    override fun getItemCount(): Int {
        return list.size
    }
}