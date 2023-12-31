package com.example.campusconnect.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.campusconnect.Adapter.CampusConnectAdapter
import com.example.campusconnect.R
import com.example.campusconnect.databinding.ActivityFindDevBinding
import com.example.campusconnect.model.model
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Locale

class FindDevActivity : AppCompatActivity() {
    lateinit var binding: ActivityFindDevBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    lateinit var drawerlayout: DrawerLayout
    lateinit var imageView: ImageView

    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: CampusConnectAdapter//

    //    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<model>


    //handling back button clicl
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
            showDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_find_dev)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_dev)


        // adding onbackpressed callback listener.
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        binding.chat.setOnClickListener {
            val intent=Intent(this,ChatListActivity::class.java)
            startActivity(intent)
        }
        mAuth = Firebase.auth
        currentUser = mAuth.currentUser!!
        drawerlayout = findViewById(R.id.drawerLayout)


        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val headerView = navigationView.getHeaderView(0) // Get the header view

// Find the TextView inside the header layout
        val textView = headerView.findViewById<TextView>(R.id.view_profile_txt)
        imageView = headerView.findViewById<ImageView>(R.id.profile_image)
        val Username = headerView.findViewById<TextView>(R.id.name_tv)

        getSpecificUserData(imageView, Username)
        binding.profileImage.setOnClickListener {
//            val intent = Intent(this, NavigationDrawer::class.java)
//            intent.putExtra("openDrawer",true)
//            startActivity(intent)
            if (!drawerlayout.isDrawerOpen(GravityCompat.START)) {
                drawerlayout.openDrawer(GravityCompat.START)

            } else {
                drawerlayout.closeDrawer(GravityCompat.START)


            }

        }


// Set an OnClickListener for the TextView
        textView.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
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
        //search the devs
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })


    }

    private fun filterList(newText: String?) {
        if (newText!=null){
            val filteredList=ArrayList<model>()
            for (i in userArrayList){
                if(i.name!!.toLowerCase(Locale.ROOT).contains(newText)){
                    filteredList.add(i)

                }
            }
            if(filteredList.isEmpty()){
                Toast.makeText(this,"No Dev Found",Toast.LENGTH_SHORT).show()
            }else{

                binding.recyclerView.adapter= CampusConnectAdapter(this,filteredList)



            }

        }

    }

    //new
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
                        Log.d("USERPrabhat", "onDataChange: ${user!!.uid}")
                        if (user.uid!=mAuth.uid){
                            newlist.add(user)
                        }

                    }

                    userArrayList.clear()
                    userArrayList.addAll(newlist)


                    Log.d("Prabhat", "onDataChange: $userArrayList ")
                    binding.recyclerView.adapter =
                        CampusConnectAdapter(this@FindDevActivity, userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getSpecificUserData(imageView: ImageView, username: TextView) {


        Log.d("andupandu", "getSpecificUserData: ${mAuth.uid.toString()}")
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid.toString())
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(model::class.java)
                    Log.d("AkashKumar", "onDataChange: $user")
                    if (user != null) {
// Assuming 'user' is an instance of your model class with a 'profileImage' field containing the image URL
                        val profileImageURL = user.profileImage
                        username.text = user.name.toString()

// Load and display the image using Glide
                        Glide.with(this@FindDevActivity)
                            .load(profileImageURL)
                            .into(binding.profileImage)

                        Glide.with(this@FindDevActivity)
                            .load(profileImageURL)
                            .into(imageView)
                    }


                } else {
                    Log.d("andupandu", "onDataChange: user not found ")
                    Toast.makeText(this@FindDevActivity, "daa", Toast.LENGTH_LONG).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun showDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Are you sure?")
            setMessage("want to close the application ?")
            setPositiveButton("Yes") { _, _ -> finishAffinity() }
            setNegativeButton("No", null)
            show()
        }
    }


    /* private fun getUserData() {
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
     }*/
}