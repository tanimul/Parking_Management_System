package com.example.parkingmanagementsystem.ui.auth

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.parkingmanagementsystem.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "RegistrationActivity"
    }

    private lateinit var binding: ActivityRegistrationBinding
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.civUserImage.setOnClickListener{
            mGetContent.launch("image/*");
        }
        binding.btnRegister.setOnClickListener {

        }




    }
    //Get image extention
    fun GetFileExtention(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> // Handle the returned Uri
        imageUri = uri
        binding.civUserImage.setImageURI(imageUri)
    }
    private fun saveInfo() {
        if (imageUri != null) {
            Log.d(TAG, "saveInfo: ")
            storageReference = storageReference.child(""+System.currentTimeMillis() +"." + GetFileExtention(imageUri))
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val url = uri.toString()
                        val userinfo = Info(binding.etName.text.toString(),url)

                        Log.d(TAG, "Successfully added")
                        Toast.makeText(
                            this,
                            "Successfully added",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }
                }.addOnFailureListener { e ->

                    Log.d(TAG, "" + e.message)
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

}