package com.example.campusconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.campusconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
       // setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        binding.viewModel = viewModel

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.connectWithVisibility.set(View.INVISIBLE)
            viewModel.splashLayoutVisibility.set(View.VISIBLE)
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
//            finish()
        }, 3000)
       // binding.viewModel.onRegisterClick(it)


    }
}