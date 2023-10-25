package com.example.campusconnect.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.campusconnect.Adapter.CertificateAdapter
import com.example.campusconnect.Adapter.ProjectAdapter
import com.example.campusconnect.R
import com.example.campusconnect.Adapter.SkillsAdapter
import com.example.campusconnect.databinding.ActivityProfileBinding
import com.example.campusconnect.model.CertificateModel
import com.example.campusconnect.model.ProjectModel
import com.example.campusconnect.model.model
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class Profile : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    private lateinit var binding: ActivityProfileBinding
    lateinit var add: CardView
    lateinit var add_certificate: CardView
    lateinit var project_name_et: TextInputEditText
    lateinit var project_url: TextInputEditText
    lateinit var certificate_name_et: TextInputEditText
    lateinit var certificate_url: TextInputEditText
    private lateinit var projectArrayList: ArrayList<ProjectModel>
    private lateinit var certificateArrayList: ArrayList<CertificateModel>
    private var skillsList = mutableListOf<String>()


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //showing dialog and then closing the application..
          val intent= Intent(this@Profile,FindDevActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        mAuth = Firebase.auth
        currentUser = mAuth.currentUser!!
        getUserData()

        // adding onbackpressed callback listener.
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        //adding the project in the recyler view
        try {

            binding.projectRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.projectRecyclerView.setHasFixedSize(true)
            projectArrayList = arrayListOf<ProjectModel>()
            getProjectDetails()

        } catch (e: Exception) {
            e.printStackTrace()
        }


        //adding the certificate in the recyler view
        try {

            binding.certificateRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.certificateRecyclerView.setHasFixedSize(true)
            certificateArrayList = arrayListOf<CertificateModel>()
            getCertificateDetails()

        } catch (e: Exception) {
            e.printStackTrace()
        }


        //opening add project
        val dialog = Dialog(this)
        binding.addProject.setOnClickListener {
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            dialog.window?.attributes?.windowAnimations = R.style.animation

            // Move the initialization of the EditText fields here, inside the click listener
            project_name_et = dialog.findViewById(R.id.project_name_et)
            project_url = dialog.findViewById(R.id.project_url_et)

            add = dialog.findViewById(R.id.add)
            add.setOnClickListener {
                // Retrieve the text from the EditText fields when the button is clicked
                val ProjectName = project_name_et.text.toString()
                val ProjectUrl = project_url.text.toString()

                if (ProjectName.isNotEmpty() && ProjectUrl.isNotEmpty()) {
                    Toast.makeText(this, "button is clicked", Toast.LENGTH_SHORT).show()
                    uploadProjectDetails(ProjectName, ProjectUrl)
                    dialog.dismiss()
                } else {
                    // Handle the case where ProjectName or ProjectUrl is empty
                    Toast.makeText(
                        this,
                        "ProjectName and ProjectUrl must not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            dialog.show()
        }

        val certificate_dialog = Dialog(this)
        binding.addCertificateIcon.setOnClickListener {
            certificate_dialog.setContentView(R.layout.certificate_custom_dialog)
            certificate_dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            certificate_dialog.setCancelable(false)
            certificate_dialog.window?.attributes?.windowAnimations = R.style.animation
            // Move the initialization of the EditText fields here, inside the click listener
            certificate_name_et = certificate_dialog.findViewById(R.id.certificate_name_et)
            certificate_url = certificate_dialog.findViewById(R.id.certificate_url_et)

            add_certificate = certificate_dialog.findViewById(R.id.add_certificate)
            add_certificate.setOnClickListener {
                // Retrieve the text from the EditText fields when the button is clicked
                val CertificateName = certificate_name_et.text.toString()
                val CertificatetUrl = certificate_url.text.toString()

                if (CertificateName.isNotEmpty() && CertificatetUrl.isNotEmpty()) {
                    certificate_dialog.dismiss()
                    uploadCertificateDetails(CertificateName, CertificatetUrl)
                    Toast.makeText(this, "project added", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle the case where ProjectName or ProjectUrl is empty
                    Toast.makeText(
                        this,
                        "ProjectName and ProjectUrl must not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            certificate_dialog.show()
        }

        //setting the skills

        try {

            binding.rvSkills.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rvSkills.setHasFixedSize(true)
            skillsList = mutableListOf<String>()


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun getUserData() {

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid.toString())
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()) {


                    val user = snapshot.getValue(model::class.java)

                    Log.d("Dubeyji", "onDataChange: $user")
                    if (user != null) {
//                        val name = user.name
                        binding.nameTv.text = user.name.toString()
                        binding.about.text = user.about.toString()
                        binding.collegeName.text = user.collegename.toString()
                        binding.collegeNameEducation.text = user.collegename.toString()
                        binding.courseName.text = user.coursename.toString()
                        val skills = user.skills
                        val newList: MutableList<String> = ArrayList()

                        Log.d("dubeyji", "onDataChange: $skills")

                        val skillsArray = skills?.split(", ")
                        if (skillsArray != null) {
                            newList.addAll(skillsArray)
                        }
                        skillsList.clear()
                        skillsList.addAll(newList)
                        Log.d("amla", "onDataChange: $skillsList")

                        val tempAdapter = SkillsAdapter(this@Profile, skillsList)
                        binding.rvSkills.adapter = tempAdapter
                        tempAdapter.notifyDataSetChanged()


//                        binding.skills1.text = user.skills.toString()
// Assuming 'user' is an instance of your model class with a 'profileImage' field containing the image URL
                        val profileImageURL = user.profileImage

// Load and display the image using Glide
                        Glide.with(this@Profile)
                            .load(profileImageURL)
                            .into(binding.profileImage)


                    }


                } else {
                    Toast.makeText(this@Profile, "daa", Toast.LENGTH_LONG).show()


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //    private fun uploadProjectDetails(projeName:String,projectUrl:String){
//
//        databaseReference=FirebaseDatabase.getInstance().getReference("Projects").child(mAuth.uid.toString())
//        val item=ProjectModel(mAuth.uid.toString(),projeName, projectUrl)
//        val projectKey = databaseReference.push().key
//        databaseReference.child(mAuth.uid.toString()).setValue(item).addOnSuccessListener{
//            Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(this, "Failed added", Toast.LENGTH_SHORT).show()
//        }
//    }
    private fun uploadProjectDetails(projectName: String, projectUrl: String) {
        // Reference to the user's UID node under "Users" in Firebase
        val userReference =
            FirebaseDatabase.getInstance().getReference("AllProjects").child(mAuth.uid.toString())

        // Reference to the "Projects" node under the user's UID
        val projectsReference = userReference.child("Projects")

        // Generate a unique key for each project (e.g., using push())
        val projectKey = projectsReference.push().key

        if (projectKey != null) {
            val item = ProjectModel(mAuth.uid.toString(), projectName, projectUrl)

            // Use the generated projectKey as the child key to store the project data
            projectsReference.child(projectKey).setValue(item).addOnSuccessListener {
                Toast.makeText(this, "Project added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to add project", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadCertificateDetails(certificateName: String, certificateUrl: String) {
        // Reference to the user's UID node under "Users" in Firebase
        val userReference =
            FirebaseDatabase.getInstance().getReference("AllCertificates")
                .child(mAuth.uid.toString())

        // Reference to the "Certificate" node under the user's UID
        val certificateReference = userReference.child("Certificates")

        // Generate a unique key for each project (e.g., using push())
        val certificateKey = certificateReference.push().key

        if (certificateKey != null) {
            val item = CertificateModel(mAuth.uid.toString(), certificateName, certificateUrl)

            // Use the generated projectKey as the child key to store the project data
            certificateReference.child(certificateKey).setValue(item).addOnSuccessListener {
                Toast.makeText(this, "Certificate added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to add Certificate", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getProjectDetails() {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("AllProjects").child(mAuth.uid.toString())
                .child("Projects")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val newlist = ArrayList<ProjectModel>()
                    for (projectSnapshot in snapshot.children) {

                        val project = projectSnapshot.getValue(ProjectModel::class.java)
                        Log.d("COugh", "onDataChange: $project")
                        if (project != null) {
                            newlist.add(project)
                        }
                    }
                    projectArrayList.clear()
                    projectArrayList.addAll(newlist)
                    binding.projectRecyclerView.adapter =
                        ProjectAdapter(this@Profile, projectArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        )
    }

    private fun getCertificateDetails() {
        Log.d("darling", "getCertificateDetails: called")
        databaseReference = FirebaseDatabase.getInstance().getReference("AllCertificates")
            .child(mAuth.uid.toString()).child("Certificates")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val newlist = ArrayList<CertificateModel>()
                    for (certificateSnapshot in snapshot.children) {


                        val certificate = certificateSnapshot.getValue(CertificateModel::class.java)
                        Log.d("COugh", "onDataChange: $certificate")
                        if (certificate != null) {
                            newlist.add(certificate)
                        }
                    }
                    Log.d("darling", "onDataChange: $newlist")
                    certificateArrayList.clear()
                    certificateArrayList.addAll(newlist)
                    Log.d("darling", "onDataChange: $certificateArrayList")
                    binding.certificateRecyclerView.adapter =
                        CertificateAdapter(this@Profile, certificateArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        )
    }


    private fun uploadCertificateDetails(
        certificateName: TextInputEditText,
        certificatetUrl: TextInputEditText
    ) {
        val ProjectName = certificateName.text.toString()
        val ProjectUrl = certificatetUrl.text.toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("Certificates")
        val item = CertificateModel(mAuth.uid.toString(), ProjectName, ProjectUrl)
        databaseReference.child(mAuth.uid.toString()).setValue(item).addOnSuccessListener {
            Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed added", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}