package com.example.campusconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.campusconnect.databinding.ActivityFindDevBinding

class FindDevActivity : AppCompatActivity() {
    lateinit var binding:ActivityFindDevBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_find_dev)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_find_dev)

        binding.profileImage.setOnClickListener {
            val intent=Intent(this,UserProfile::class.java)
            startActivity(intent)

        }

    }
}