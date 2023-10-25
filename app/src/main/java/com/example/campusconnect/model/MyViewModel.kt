package com.example.campusconnect.model

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import com.example.campusconnect.ui.SignUpActivity

class MyViewModel : ViewModel() {
    val connectWithVisibility = ObservableField(View.VISIBLE)
    val splashLayoutVisibility = ObservableField(View.INVISIBLE)
    fun onRegisterClick(view: View) {
        val intent = Intent(view.context, SignUpActivity::class.java)
        view.context.startActivity(intent)
    }
}
