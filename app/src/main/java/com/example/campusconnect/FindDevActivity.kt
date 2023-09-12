package com.example.campusconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusconnect.databinding.ActivityFindDevBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class FindDevActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindDevBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser

    private lateinit var databaseReference: DatabaseReference

    //    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<model>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_find_dev)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_dev)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser!!

        binding.profileImage.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)

        }
        try {
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.setHasFixedSize(true)
            userArrayList = arrayListOf<model>()

            /*val skillArray = userArrayList.get(0).skills?.split(",")
            Log.d("SKILL_LIST", "onCreate: " + skillArray.toString())*/

            getUserData()
        } catch (e: Exception) {
            Log.d("Prabhat", "onCreate: catch block " + e.printStackTrace())
            e.printStackTrace()
        }


    }

    private fun getUserData() {
        Log.d("Prabhat", "getUserData called")
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Prabhat", "onDataChange: inside it")
                val newlist = ArrayList<model>()

                if (snapshot.exists()) {
                    Log.d("Prabhat", "onDataChange: if snapshot exist ")
                    Log.d("Prabhat", "onDataChange: ${snapshot.children.iterator()}")
                    for (userSnapshot in snapshot.children) {

                        val user = userSnapshot.getValue(model::class.java)
                        Log.d("Prabhat", "onDataChange: $user")
//                        userArrayList.add(user!!)
                        newlist.add(user!!)

                    }
                    userArrayList.clear()
                    userArrayList.addAll(newlist)
                    Log.d("Prabhat", "onDataChange: $userArrayList ")
                    binding.recyclerView.adapter =
                        CampusConnectAdapter(applicationContext, userArrayList)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}