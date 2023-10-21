package com.example.campusconnect.Adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusconnect.R
import com.example.campusconnect.databinding.DeleteLayoutBinding
import com.example.campusconnect.databinding.ReceiveMsgBinding
import com.example.campusconnect.databinding.SendMsgBinding
import com.example.campusconnect.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(

    //
    val context: Context,
    messages: ArrayList<Message>?,
    senderRoom: String,
    receiverRoom: String,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {


    lateinit var messages: ArrayList<Message>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2
    var senderRoom: String
    var receiverRoom: String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return if (viewType == ITEM_SENT) {
            Log.d("PRABHATMESSAGEADAPTER", "onCreateViewHolder: is called ")


            /*val view = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SentMsgHolder(view)*/
            SentMsgHolder(SendMsgBinding.inflate(LayoutInflater.from(context),parent,false))
        } else {
            /*val view =
                LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiveMsgHolder(view)*/
            ReceiveMsgHolder(ReceiveMsgBinding.inflate(LayoutInflater.from(context),parent,false))

        }

    }

    override fun getItemViewType(position: Int): Int {
        val messages = messages[position]
        return if (FirebaseAuth.getInstance().uid == messages.senderId) {
            Log.d("PRABHATMESSAGEADAPTER", "onCreateViewHolder: is called ")
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val message = messages[position]
        Log.d("MessageAdapter", "Messages List Size: ${messages.size}")

        if (holder is SentMsgHolder) {
            val viewHolder = holder as SentMsgHolder
            if (message.message.equals("photo")) {
//                viewHolder.binding.image.visibility = View.VISIBLE
//                viewHolder.binding.message.visibility = View.GONE
//                viewHolder.binding.mLinear.visibility = View.GONE
//                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder)
//                    .into(viewHolder.binding.image)

                //my code
                holder.image.visibility=View.VISIBLE
                holder.message.visibility=View.GONE
                holder.mLinear.visibility=View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder).into(holder.image)
            }

//            viewHolder.binding.message.text = message.message
            //mycode
            holder.message.text=message.message
            Log.d("SenderMessage", "Sender's Message: ${message.message}")
            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater
                    .from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog: AlertDialog =
                    AlertDialog.Builder(context).setTitle("Delete Message").setView(binding.root)
                        .create()
                binding.everyone.setOnClickListener {

                    message.message = "This message is removed"
                    message.messageId?.let { it1 ->
                        Log.d("TAGit1sender", "onBindViewHolder: ${it1}")
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(it1).setValue(message)

                    }
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(it1!!).setValue(message)

                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {

                    message.messageId.let { it1 ->

                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()

                }
                binding.cancel.setOnClickListener {


                    dialog.dismiss()

                }
                dialog.show()
                false
            }

        } else if (holder is ReceiveMsgHolder) {

            val viewHolder = holder as ReceiveMsgHolder
            if (message.message.equals("photo")) {
                /*viewHolder.binding.image.visibility = View.VISIBLE
                viewHolder.binding.message.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.image)*/

                //my code
                holder.image.visibility=View.VISIBLE
                holder.message.visibility=View.GONE
                holder.mLinear.visibility=View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder).into(holder.image)


            }
//            viewHolder.binding.message.text = message.message
            //mycode
            holder.message.text=message.message
            viewHolder.itemView.setOnClickListener {
                val view = LayoutInflater
                    .from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog: AlertDialog =
                    AlertDialog.Builder(context).setTitle("Delete Message").setView(binding.root)
                        .create()
                binding.everyone.setOnClickListener {

                    message.message = "This message is removed"
                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(it1).setValue(message)
//                            .child(it1).setValue(message)

                    }
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(it1!!).setValue(message)

                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {

                    message.messageId.let { it1 ->

                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()

                }
                binding.cancel.setOnClickListener {


                    dialog.dismiss()

                }
                dialog.show()
//                false
            }

        }
    }

    override fun getItemCount(): Int {
        return messages.size


    }


    /* inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var binding: SendMsgBinding = SendMsgBinding.bind(itemView)

     }

     inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
     }*/
    inner class SentMsgHolder(binding: SendMsgBinding) : RecyclerView.ViewHolder(binding.root) {
//       var binding: SendMsgBinding = SendMsgBinding.bind(itemView)


        val mLinear = binding.mLinear
        val message = binding.message
        val image = binding.image
    }

    inner class ReceiveMsgHolder(binding: ReceiveMsgBinding) : RecyclerView.ViewHolder(binding.root) {
        //        var binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
        val mLinear = binding.mLinear
        val message = binding.message
        val image = binding.image
    }

    init {

        this.messages = messages!!

        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom

    }


}