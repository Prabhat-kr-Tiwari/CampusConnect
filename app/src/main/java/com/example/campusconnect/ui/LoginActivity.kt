package com.example.campusconnect.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.campusconnect.R
import com.example.campusconnect.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mAuth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {

            val intent = Intent(this, FindDevActivity::class.java)
            startActivity(intent)
            finish()
        }
//        else{
//            val intent = Intent(this, UserProfile::class.java)
//            startActivity(intent)
//            finish()
//
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()


        binding.register.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.loginBtn.setOnClickListener {

            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter the Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter the Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, FindDevActivity::class.java)
                        startActivity(intent)
//                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }


        }

    }
}