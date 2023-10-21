package com.example.campusconnect.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnect.databinding.ItemProjectBinding
import com.example.campusconnect.model.ProjectModel

class ProjectAdapter(private val context: Context,private val list: List<ProjectModel>)
    :RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    inner  class ViewHolder(binding:ItemProjectBinding):RecyclerView.ViewHolder(binding.root){

        val projectName=binding.ProjectName
        val projectUrl=binding.ProjectUrl
        val cardview=binding.projectCardView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ViewHolder {

        return ViewHolder(
            ItemProjectBinding.inflate(LayoutInflater.from(context),parent,false)
        )

    }
    override fun onBindViewHolder(holder: ProjectAdapter.ViewHolder, position: Int) {

        val model=list[position]
        holder.projectName.text=model.projectname
        val view_project_text="View Project"
        holder.projectUrl.text=view_project_text
        holder.cardview.setOnClickListener {
            val projectLink=model.projecturl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(projectLink))
            val context = holder.itemView.context // Get the context from the item view
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}