package com.example.campusconnect.ui


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils

import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.campusconnect.R
import com.example.campusconnect.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var mAuth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {

            val intent=Intent(this, FindDevActivity::class.java)
            startActivity(intent)
//            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        mAuth=FirebaseAuth.getInstance()
        binding.login.setOnClickListener {
            val intent =Intent(this, LoginActivity::class.java)
            startActivity(intent)
//            finish()
        }

//hello
        binding.signUpbtn.setOnClickListener {
            val fullName=binding.fullNameEt.text.toString()
            val email=binding.emailEt.text.toString()
            val password=binding.passwordEt.text.toString()
            val cnfPassword=binding.confirmPassEt.text.toString()
            if(TextUtils.isEmpty(fullName)){
                Toast.makeText(this, "Enter the name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Enter the Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "Enter the Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(cnfPassword)){
                Toast.makeText(this, "Enter the Confirm Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password==cnfPassword){
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
//                        val user = mAuth.currentUser
                            Toast.makeText(
                                baseContext,
                                "Account created.",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val intent = Intent(this, UserProfile::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }

            }else{
                Toast.makeText(this, "Password and Confirm password cannot matched", Toast.LENGTH_LONG).show()
            }


        }
    }
}