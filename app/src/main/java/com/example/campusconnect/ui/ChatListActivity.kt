package com.example.campusconnect.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusconnect.Adapter.CampusConnectAdapter
import com.example.campusconnect.Adapter.ChatListAdapter
import com.example.campusconnect.R
import com.example.campusconnect.databinding.ActivityChatBinding
import com.example.campusconnect.databinding.ActivityChatListBinding
import com.example.campusconnect.model.ProjectModel
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
import java.lang.Exception
import java.util.Locale
import kotlin.math.log

class ChatListActivity : AppCompatActivity() {
    lateinit var binding:ActivityChatListBinding
    private lateinit var databaseReference: DatabaseReference
     lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    val arrayList = ArrayList<String>()
    var uniqueList=ArrayList<String>()
    var userwithchats= ArrayList<model>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_chat_list)
//        setContentView(R.layout.activity_chat_list)
        mAuth = Firebase.auth
        currentUser = mAuth.currentUser!!



        binding.backBtn.setOnClickListener {
            finish()
        }

        try {
           /* binding.projectRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.projectRecyclerView.setHasFixedSize(true)
            projectArrayList = arrayListOf<ProjectModel>()
            getProjectDetails()*/
            binding.recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            binding.recyclerView.setHasFixedSize(true)
            userwithchats=arrayListOf<model>()
            getListOfChat()


        }catch (e:Exception){
            e.printStackTrace()
        }

        //search the devs
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

    }
    fun getListOfChat() {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("chats")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("MYUID", "onDataChange: ${mAuth.uid.toString()}")

                if(snapshot.exists()){
                    arrayList.clear()
                    Log.d("chatlist", "onDataChange: ${snapshot}")
                    for (snap in snapshot.children) {
                        val key = snap.key
//                        val dataMap = snap.getValue() as? Map<String, Any>
                        Log.d("DaRLING", "onDataChange: ${key}")
                        if (key!!.contains(mAuth.uid.toString())){
                            Log.d("GODUBABA", "onDataChange: ${key}")
                            val  myuid=mAuth.uid.toString()
                            val newstr=key.replace(myuid,"")
                            arrayList.add(newstr)
                            uniqueList = arrayList.toSet().toMutableList() as ArrayList<String>


                        }



                    }
                }
                Log.d("BABUJI", "onDataChange: ${arrayList}")
                Log.d("BABUJI", "onDataChange: ${uniqueList}")
                processUniqueList(uniqueList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





    }
  /*  private fun processUniqueList(uniqueList: ArrayList<String>) {
        // Do something with uniqueList here
        Log.d("RAJAJI", "onCreate: $uniqueList")


        for(user in uniqueList){


            databaseReference =
                FirebaseDatabase.getInstance().getReference("Users").child(user)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        Log.d("SNAPJI", "onDataChange: ${snapshot}")

                       val users =snapshot.getValue(model::class.java)
                        Log.d("USERJI", "onDataChange: ${users}")
                        var newList=ArrayList<model>()
                        if (users != null) {
                            Log.d("USERPRESENt", "onDataChange: user is prersent")

                            newList.add(users)


                        }else{
                            Log.d("USERJINULL", "onDataChange: user is null")
                        }
                        userwithchats.clear()
                        userwithchats.addAll(newList)
                        Log.d("userWithChats", "processUniqueList: ${userwithchats.size}")

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })
        }
        Log.d("userWithChats", "processUniqueList: ${userwithchats.size}")





    }*/
    private fun processUniqueList(uniqueList: ArrayList<String>) {
        val newList = ArrayList<model>() // Create a single list to accumulate user data

        for (user in uniqueList) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val users = snapshot.getValue(model::class.java)
                        if (users != null) {
                            newList.add(users)
                            Log.d("USERPRESENT", "onDataChange: user is present")
                        } else {
                            Log.d("USERJINULL", "onDataChange: user is null")
                        }

                        // Check if the size of newList matches the size of uniqueList
                        if (newList.size == uniqueList.size) {
                            userwithchats.clear()
                            userwithchats.addAll(newList)
                            Log.d("userWithChats", "processUniqueList: ${userwithchats}")
                        }

                        val adapter=ChatListAdapter(this@ChatListActivity,userwithchats)
                        binding.recyclerView.adapter=adapter
                        adapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled
                }
            })
        }
    }
    private fun filterList(newText: String?) {
        if (newText!=null){
            val filteredList=ArrayList<model>()
            for (i in userwithchats){
                if(i.name!!.toLowerCase(Locale.ROOT).contains(newText)){
                    filteredList.add(i)

                }
            }
            if(filteredList.isEmpty()){
                Toast.makeText(this,"No Dev Found", Toast.LENGTH_SHORT).show()
            }else{

                binding.recyclerView.adapter= ChatListAdapter(this,filteredList)



            }

        }

    }




}