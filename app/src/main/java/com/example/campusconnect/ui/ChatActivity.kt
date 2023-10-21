package com.example.campusconnect.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.campusconnect.Adapter.MessageAdapter
import com.example.campusconnect.R
import com.example.campusconnect.databinding.ActivityChatBinding
import com.example.campusconnect.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Date
import kotlin.math.log

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    var adapter: MessageAdapter? = null
    var messages: ArrayList<Message>? = null
    var senderRoom: String? = null
    var receiverRoom: String? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog? = null
    var senderUid: String? = null
    var receiverUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chat)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
//        binding=ActivityChatBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messages = ArrayList()
        Log.d("andupandu2", "onCreate: ${messages}")
        val name = intent.getStringExtra("name")
        binding.name.text = name
        val profile = intent.getStringExtra("image")
        Glide.with(this).load(profile).placeholder(R.drawable.placeholder).into(binding.profile01)
        binding.backArrow.setOnClickListener {
            finish()
        }
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid

        receiverUid?.let {
            database!!.reference.child("Presence").child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {


                        if (snapshot.exists()) {
                            val status = snapshot.getValue(String::class.java)
                            if (status == "offline") {
                                binding.status.visibility = View.GONE

                            } else {
                                binding.status.setText(status)
                                binding.status.visibility = View.VISIBLE

                            }
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
            senderRoom = senderUid + receiverUid
            receiverRoom = receiverUid + senderUid
        Log.d("mackbook", "onCreate: ${messages!!.size}")
            adapter = MessageAdapter(this, messages!!, senderRoom!!, receiverRoom!!)

            binding.recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = adapter
            database!!.reference.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        messages!!.clear()
                        for (snapshot1 in snapshot.children) {
//                           val message: Message? = snaapshot1.getValue(Message::class.java)
//                            message!!.messageId=snaapshot1.key

                            val messageSnapshot = snapshot1.getValue(Message::class.java)
                            Log.d("ReceiverMessage", "onDataChange: ${messageSnapshot}")
//                            val messageId = snapshot1.key
//                            val message: Message
//
//                            message = Message(
//                                messageSnapshot?.message,
//                                messageSnapshot?.senderId,
//                                messageSnapshot?.timeStamp ?: 0
//                            )
                            messages!!.add(messageSnapshot!!)

                        }
                        Log.d("mackbook", "onCreate: ${messages!!.size}")

                        adapter!!.notifyDataSetChanged()

                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            binding.send.setOnClickListener {


                val messageTxt: String = binding.messageBox.text.toString()
                val date = Date()
                val message = Message(messageTxt, senderUid, date.time)
                binding.messageBox.setText("")
                // Log the sent message
                Log.d("ChatActivityMessageSend", "Sent message: ${message.message}")
                val randomKey = database!!.reference.push().key
                val lastMsgObj = HashMap<String, Any>()
                lastMsgObj["lastMsg"] = message.message!!
                lastMsgObj["lastMsgTime"] = date.time

               /* database!!.reference.child("chats")
                    .updateChildren(lastMsgObj)*/


                database!!.reference.child("chats").child(senderRoom!!)
                    .updateChildren(lastMsgObj)
                database!!.reference.child("chats").child(receiverRoom!!)
                    .updateChildren(lastMsgObj)


                Log.d("hiiiiiiiiiii", "onCreate: ${message.message}")
                database!!.reference.child("chats").child(senderRoom!!)
                    .child("messages")
                    .child(randomKey!!)
                    .setValue(message).addOnSuccessListener {
                        database!!.reference.child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .child(randomKey)
                            .setValue(message)
                            .addOnSuccessListener {

                                Log.d("ChatActivity", "Sent message: ${message.message}")
                            }
                    }


            }

            binding.attachment.setOnClickListener {


                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 25)


            }
            val handler = Handler()
            binding.messageBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {


                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {


                    database!!.reference.child("Presence")
                        .child(senderUid!!)
                        .setValue("typying..")
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(userStoppedTyping, 1000)

                }

                var userStoppedTyping = Runnable {
                    database!!.reference.child("Presence")
                        .child(senderUid!!)
                        .setValue("Online")


                }

            })
            supportActionBar!!.setDisplayShowTitleEnabled(false)



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calender = Calendar.getInstance()
                    var refence = storage?.reference?.child("chats")
                        ?.child(calender.timeInMillis.toString() + "")
                    dialog?.show()
                    if (refence != null) {
                        refence.putFile(selectedImage!!)
                            .addOnCompleteListener { task ->
                                dialog?.dismiss()
                                if (task.isSuccessful) {
                                    refence.downloadUrl.addOnSuccessListener { uri ->
                                        val filePath = uri.toString()
                                        val messageTxt: String = binding.messageBox.text.toString()
                                        val date = Date()
                                        val message = Message(messageTxt, senderUid, date.time)
                                        message.message = "photo"
                                        message.imageUrl = filePath
                                        binding.messageBox.setText("")
                                        val randomKey = database!!.reference.push().key
                                        val lastMsgObj = HashMap<String, Any>()
                                        lastMsgObj["lastMsg"] = message.message!!
                                        lastMsgObj["lastMsgTime"] = date.time
                                        database!!.reference.child("chats")
                                            .updateChildren(lastMsgObj)

                                        database!!.reference.child("chats")
                                            .child(receiverRoom!!).updateChildren(lastMsgObj)

                                        database!!.reference.child("chats")
                                            .child(senderRoom!!).child("messages")
                                            .child(randomKey!!)
                                            .setValue(message).addOnSuccessListener {
                                                database!!.reference.child("chats")
                                                    .child(receiverRoom!!)
                                                    .child("messages")
                                                    .child(randomKey)
                                                    .setValue(message)
                                                    .addOnSuccessListener {

                                                    }
                                            }

                                    }

                                }


                            }
                    }


                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        if (currentId != null) {
            database?.reference?.child("Presence")
                ?.child(currentId)
                ?.setValue("Online")
        }

    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        if (currentId != null) {
            database?.reference?.child("Presence")
                ?.child(currentId)
                ?.setValue("offline")
        }
    }
}