package com.example.campusconnect

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.campusconnect.databinding.ActivityNavigationDrawerBinding
import com.example.campusconnect.model.model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class NavigationDrawer : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityNavigationDrawerBinding
    lateinit var profileImage:ImageView
    lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser

    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("spji", "onCreate: ")
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_navigation_drawer)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_navigation_drawer)
        mAuth = Firebase.auth
        currentUser = mAuth.currentUser!!


        val intent = intent
        val openDrawer = intent.getBooleanExtra("openDrawer", false)
        toolbar=findViewById(R.id.toolbar)
        profileImage=findViewById(R.id.profile_image)



        //step 1
        setSupportActionBar(toolbar)
       val toogle: ActionBarDrawerToggle=
           ActionBarDrawerToggle(this,binding.drawerLayout,toolbar,R.string.OpenDrawer,R.string.CloseDrawer)
        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
        //getUserData(profileImage)

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.groups -> {
                    // Handle the "Groups" item click
                }
                R.id.events -> {
                    // Handle the "Events" item click
                }
                else -> {
                    // Handle other item clicks
                }
            }

            // Close the drawer if needed
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.closeDrawer(GravityCompat.START)

            true // Return true to indicate that the item click has been handled
        }

        // Open the drawer if the openDrawer flag is true
        if (openDrawer) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }


    }
    private fun getUserData(proileImage:ImageView) {
        Log.d("spji", "navigation: ")
        Log.d("Prabhat", "getUserData called")
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid.toString())
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Prabhat", "onDataChange: inside it")
                val newlist = ArrayList<model>()

                if (snapshot.exists()) {
                    Log.d("Prabhat", "onDataChange: if snapshot exist ")
                    Log.d("Prabhat", "onDataChange: ${snapshot.children.iterator()}")
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(model::class.java)
                        Log.d("Aksh", "onDataChange: $user")

                        val profileImageURL = user?.profileImage
                        Log.d("spji", "onDataChange: $profileImageURL")

                        Glide.with(this@NavigationDrawer)
                            .load(profileImageURL)
                            .into(proileImage)

                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}