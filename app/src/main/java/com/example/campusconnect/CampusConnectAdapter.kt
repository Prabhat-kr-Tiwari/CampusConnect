package com.example.campusconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusconnect.databinding.ItemBinding

class CampusConnectAdapter(private val context: Context,
    private val list: ArrayList<model>
) : RecyclerView.Adapter<CampusConnectAdapter.ViewHolder>() {


    // step 2
    private var onClickListener: View.OnClickListener? = null

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class ViewHolder(binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val profileImage = binding.profileImage
        val name = binding.name
        val coursename = binding.courseName
        val collegename = binding.collegeName
        val about = binding.about
        val githuburl = binding.githubUrl
        val linkdinurl = binding.linkdinUrl
        val skills = binding.skill1
        val expertise = binding.expertiseIn


    }

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    // step 3
    // next one is function which binds the onclicklistener
    // adapter does not have on clicklistener so we need to implement here
    /* fun setOnClickListener(onClickListener: OnClickListener){
         this.onClickListener=onClickListener
     }*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]
//        holder.profileImage.setImageURI(Uri.parse(model.profileImage))
        Glide.with(context).load(model.profileImage).into(holder.profileImage)
        holder.name.text=model.name
        holder.coursename.text=model.coursename
        holder.collegename.text=model.collegename
        holder.about.text=model.about
//        holder.githuburl.text=model.githuburl
//        holder.linkdinurl.text=model.linkdinurl
        holder.skills.text=model.skills
        holder.expertise.text=model.expertise
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