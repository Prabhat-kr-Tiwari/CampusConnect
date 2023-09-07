package com.example.campusconnect

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.campusconnect.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID


class UserProfile : AppCompatActivity(), View.OnClickListener {
    private lateinit var db: FirebaseDatabase
    lateinit var binding: ActivityUserProfileBinding
    private lateinit var database: DatabaseReference
    private var saveImageToInternalStorage: Uri? = null
    private var model: model? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        //FirebaseApp.initializeApp(this)
        Log.d("Prabhatji", "inside oncreate")
        //
        // Write a message to the database
        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        // Read from the database
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                Log.d("dubeyji", "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Tiwariji", "Failed to read value.", error.toException())
            }
        })


        //

        binding.profileImage.setOnClickListener {
            val picturedialog = AlertDialog.Builder(this)
            picturedialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select From galary", "Capture Photo from Camera")
            picturedialog.setItems(pictureDialogItems) {

                    dialog, which ->
                when (which) {
                    0 -> {
                        choosePhotoFromGalary()
                        Toast.makeText(
                            this, "Select From galary",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    1 -> {
                        takePhotoFromCamera()
                    }
                }

            }
            picturedialog.show()


        }
        binding.Updatee.setOnClickListener(this)


    }

    private fun saveUserDataToDB(
        image: String,
        coursename: String,
        collegename: String,
        about: String,
        githuburl: String,
        linkdinurl: String,
        skills: String,
        expertise: String
    ) {
        val userMap = hashMapOf<String, Any>(
            "image" to image,
            "courseName" to coursename,
            "collageName" to collegename,
            "about" to about,
            "githubUrl" to githuburl,
            "linkedinUrl" to linkdinurl,
            "skills" to skills,
            "expertise" to expertise
        )
        Log.d("Prabhatdon", userMap.toString())
        db = FirebaseDatabase.getInstance()
        Log.d("Prabhatdon", "FirebaseDatabase")
        val userReference: DatabaseReference = db.getReference("items")
        Log.d("Prabhatdon", "userReference")

        val mAuth = FirebaseAuth.getInstance()
        Log.d("Prabhatdon", "mAuth")


        val currentUser = mAuth.currentUser?.displayName
        userReference.child(currentUser.toString()).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lofMessage("Sucess ho gya")
                    Toast.makeText(this@UserProfile, "Update Success", Toast.LENGTH_SHORT).show()
                } else {
                    lofMessage("fail ho gya")

                    Toast.makeText(this@UserProfile, "Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                lofMessage("update fail ho gya")
                Toast.makeText(this@UserProfile, "Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun lofMessage(mesage: String) {
        Log.d("PRABHAT", "lofMessage: $mesage")
    }

    //from camera
    private fun takePhotoFromCamera() {
        if (allPermissionsGranted()) {
            //viewModel.startCamera(enableVideoBtn, enableImageBtn, autoCapture)
            Toast.makeText(this, "Read and write are granted", Toast.LENGTH_SHORT).show()

            val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            startActivityForResult(galleryIntent, CAMERA)

        } else {
            showRationalDialogPermission()
            /* ActivityCompat.requestPermissions(
                 this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
             )*/
        }

    }

    //TAking photo from gallaery
    private fun choosePhotoFromGalary() {


        if (allPermissionsGranted()) {
            //viewModel.startCamera(enableVideoBtn, enableImageBtn, autoCapture)
            Toast.makeText(this, "Read and write are granted", Toast.LENGTH_SHORT).show()

            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(galleryIntent, GALLERY)

        } else {
            showRationalDialogPermission()
            /* ActivityCompat.requestPermissions(
                 this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
             )*/
        }

    }

    // Taking Camera Permission
    private fun showRationalDialogPermission() {
        AlertDialog.Builder(this).setMessage("You have turned of the permission")
            .setPositiveButton("Got tot setting") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // viewModel.startCamera(enableVideoBtn, enableImageBtn, autoCapture)
            } else {
                /* UtilityFunctions.showToast(
                     this,
                     "Permissions not granted by the user.",
                 )*/
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // saving image to gallery

    private fun convertDrawableToByteArray(drawableResId: Int): ByteArray? {
        val bitmap = BitmapFactory.decodeResource(resources, drawableResId)
        return bitmap?.let { bitmapToByteArray(it) }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Toast.makeText(this, "onActivityResult is called", Toast.LENGTH_SHORT).show()

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "$requestCode", Toast.LENGTH_SHORT).show()

            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentUri = data.data
                    try {
                        Toast.makeText(this, "image get", Toast.LENGTH_LONG).show()
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)

                        saveImageToInternalStorage =
                            saveImageToInternalStorage(selectedImageBitmap)
                        Log.d("saved image", "path :: $saveImageToInternalStorage")
//                        binding.profile_image.setImageBitmap(selectedImageBitmap)
                        binding.profileImage.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            "failed to get image from gallery",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }


                }

            } else if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                Log.d("saved image", "path :: $saveImageToInternalStorage")
//                binding.profile_image.setImageBitmap(thumbnail)
                binding.profileImage.setImageBitmap(thumbnail)
            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                //here we are  getting the location from the autocomplete
//                val place:Place=Autocomplete.getPlaceFromIntent(data!!)
//                binding.etLocation.setText(place.address)
//                mLatitude= place.latLng!!.latitude
//                mLongitude= place.latLng!!.longitude
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)


    }

    companion object {
        private const val GALLERY = 1


        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

    override fun onClick(v: View?) {

        when (v!!.id) {

            R.id.Updatee -> {

                when {


                    binding.fullNameEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()
                    }

                    binding.courseEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter Course Name", Toast.LENGTH_SHORT).show()
                    }

                    binding.collegeNmaeEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter College Name", Toast.LENGTH_SHORT).show()
                    }

                    binding.aboutEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter About Your Self", Toast.LENGTH_SHORT)
                            .show()
                    }

                    binding.githubLinkEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter Github Link", Toast.LENGTH_SHORT).show()
                    }

                    binding.linkdinLinkEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter Linkdin Url", Toast.LENGTH_SHORT).show()
                    }

                    binding.skillsEt.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter About Your skills", Toast.LENGTH_SHORT)
                            .show()
                    }

                    binding.expertiseEt.text.isNullOrEmpty() -> {
                        Toast.makeText(
                            this,
                            "Please Enter in which field you expert",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()

                    }


                    else -> {

                        Log.d("prabhat", "button clicked")


                        saveImageToInternalStorage = Uri.parse(model?.profileImage)
                        binding.profileImage.setImageURI(saveImageToInternalStorage)
                        val image = saveImageToInternalStorage.toString()
//            val name=binding.fullNameEt.setText(model!!.name)
                        val name = binding.fullNameEt.text.toString()
                        Log.d("prabhat", name)
                        val coursename = binding.courseEt.text.toString()
                        val collegename = binding.collegeNmaeEt.text.toString()
                        val about = binding.aboutEt.text.toString()
                        val githuburl = binding.githubLinkEt.text.toString()
                        val linkdinurl = binding.linkdinLinkEt.text.toString()
                        val skills = binding.skillsEt.text.toString()
                        val expertise = binding.expertiseEt.text.toString()


                        database = FirebaseDatabase.getInstance().getReference("Users")
                        val item = model(
                            image,
                            name,
                            coursename,
                            collegename,
                            about,
                            githuburl,
                            linkdinurl,
                            skills,
                            expertise
                        )
                        Log.d("items", item.toString())
                        database.child(name).setValue(item).addOnSuccessListener {
                            binding.fullNameEt.text?.clear()
                            Log.d("prabhat", name)
                            binding.courseEt.text?.clear()
                            binding.collegeNmaeEt.text?.clear()
                            binding.aboutEt.text?.clear()
                            binding.githubLinkEt.text?.clear()
                            binding.linkdinLinkEt.text?.clear()
                            binding.skillsEt.text?.clear()
                            binding.expertiseEt.text?.clear()
                            Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT).show()


                        }


                    }
                }
            }

        }

    }




}