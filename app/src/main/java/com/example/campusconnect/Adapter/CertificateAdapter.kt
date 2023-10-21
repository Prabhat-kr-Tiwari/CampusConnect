package com.example.campusconnect.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnect.databinding.ItemCertificateBinding
import com.example.campusconnect.databinding.ItemProjectBinding
import com.example.campusconnect.model.CertificateModel

class CertificateAdapter(private val context: Context,private val list:List<CertificateModel>):RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {

    inner class ViewHolder(binding:ItemCertificateBinding):RecyclerView.ViewHolder(binding.root){
        val certificateName=binding.CertificateName
        val certificatetUrl=binding.CertificateUrl
        val cardview=binding.certificateCardView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateAdapter.ViewHolder {
        return ViewHolder(ItemCertificateBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CertificateAdapter.ViewHolder, position: Int) {
        val model=list[position]
        holder.certificateName.text=model.certificatename
        val view_certificate_text="View Certificate"
        holder.certificatetUrl.text=view_certificate_text
        holder.cardview.setOnClickListener {
            val certificateLink=model.certificateturl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(certificateLink))
            val context = holder.itemView.context // Get the context from the item view
            context.startActivity(intent)
        }
    }
}