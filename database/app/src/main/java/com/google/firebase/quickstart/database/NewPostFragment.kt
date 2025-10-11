package com.google.firebase.quickstart.database

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.firebase.quickstart.database.databinding.FragmentNewPostBinding
import com.google.firebase.quickstart.database.models.Post
import com.google.firebase.quickstart.database.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import java.util.*

class NewPostFragment : BaseFragment() {
    private var binding: FragmentNewPostBinding? = null
    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var uploadTask: StorageTask<*>? = null
    private var imageUris: MutableList<Uri> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        storageRef = FirebaseStorage.getInstance().reference
        binding!!.fabAddImage.setOnClickListener { openFileChooser() }
        binding!!.fabSubmitPost.setOnClickListener { submitPost() }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFiles(): List<String> {
        val fileNames: MutableList<String> = mutableListOf()
        if (imageUris.isNotEmpty()) {
            for (imageUri in imageUris) {
                val fileName = System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
                fileNames.add(fileName)
                val fileReference = storageRef.child(fileName)
                uploadTask = fileReference.putFile(imageUri)
                uploadTask!!.addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        context,
                        "image upload failure",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnSuccessListener {
                    Log.e(
                        TAG,
                        "image upload success: "
                    )
                }
            }
        } else {
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
        }
        return fileNames
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    imageUris.add(data.clipData!!.getItemAt(i).uri)
                }
            } else if (data.data != null) {
                imageUris.add(data.data!!)
            }
            if (imageUris.isNotEmpty()) {
                binding!!.imageView.setImageURI(imageUris[0])
            }
        }
    }

    private fun submitPost() {
        val warehouse = binding!!.fieldWarehouse.text.toString()
        val area = binding!!.fieldArea.text.toString()
        val row = binding!!.fieldRow.text.toString()
        val slot = binding!!.fieldSlot.text.toString()
        val number = binding!!.fieldNumber.text.toString()
        val count = binding!!.fieldCount.text.toString()
        val format = binding!!.fieldFormat.text.toString()
        val remarks = binding!!.fieldRemarks.text.toString()
        val snumber = binding!!.fieldSNumber.text.toString()
        val unit = binding!!.fieldUnit.text.toString()
        val name = binding!!.fieldName.text.toString()

        if (TextUtils.isEmpty(warehouse)) {
            binding!!.fieldWarehouse.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(area)) {
            binding!!.fieldArea.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(row)) {
            binding!!.fieldRow.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(slot)) {
            binding!!.fieldSlot.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(snumber)) {
            binding!!.fieldSNumber.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(name)) {
            binding!!.fieldName.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(format)) {
            binding!!.fieldFormat.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(unit)) {
            binding!!.fieldUnit.error = REQUIRED
            return
        }
        if (TextUtils.isEmpty(number)) {
            binding!!.fieldNumber.error = REQUIRED
            return
        }
        try {
            number.toInt()
        } catch (e: NumberFormatException) {
            binding!!.fieldNumber.error = "Must be a number"
            return
        }
        if (TextUtils.isEmpty(count)) {
            binding!!.fieldCount.error = REQUIRED
            return
        }
        try {
            count.toInt()
        } catch (e: NumberFormatException) {
            binding!!.fieldCount.error = "Must be a number"
            return
        }
        if (imageUris.isEmpty()) {
            Toast.makeText(context, "Please select at least one image", Toast.LENGTH_SHORT).show()
            return
        }
        var fileNames: List<String> = ArrayList()
        if (uploadTask != null && uploadTask!!.isInProgress) {
            Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
        } else {
            fileNames = uploadFiles()
        }
        val uploadFileNames = fileNames
        setEditingEnabled(false)
        Toast.makeText(context, "Posting...", Toast.LENGTH_SHORT).show()
        val userId = uid
        database.child("users").child(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(
                        User::class.java
                    )
                    if (user == null) {
                        Log.e(
                            TAG,
                            "User $userId is unexpectedly null"
                        )
                        Toast.makeText(
                            context,
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        writeNewPost(
                            userId,
                            user.username,
                            warehouse,
                            area,
                            row,
                            slot,
                            number,
                            count,
                            format,
                            remarks,
                            snumber,
                            unit,
                            name,
                            uploadFileNames
                        )
                    }
                    setEditingEnabled(true)
                    findNavController().navigate(R.id.action_NewPostFragment_to_MainFragment)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(
                        TAG,
                        "getUser:onCancelled",
                        databaseError.toException()
                    )
                    setEditingEnabled(true)
                }
            })
    }

    private fun setEditingEnabled(enabled: Boolean) {
        binding!!.fieldWarehouse.isEnabled = enabled
        binding!!.fieldArea.isEnabled = enabled
        binding!!.fieldRow.isEnabled = enabled
        binding!!.fieldSlot.isEnabled = enabled
        binding!!.fieldSNumber.isEnabled = enabled
        binding!!.fieldName.isEnabled = enabled
        binding!!.fieldFormat.isEnabled = enabled
        binding!!.fieldUnit.isEnabled = enabled
        binding!!.fieldNumber.isEnabled = enabled
        binding!!.fieldCount.isEnabled = enabled
        binding!!.fieldRemarks.isEnabled = enabled
        if (enabled) {
            binding!!.fabSubmitPost.show()
        } else {
            binding!!.fabSubmitPost.hide()
        }
    }

    private fun writeNewPost(
        userId: String,
        username: String?,
        warehouse: String,
        area: String,
        row: String,
        slot: String,
        number: String,
        count: String,
        format: String,
        remarks: String,
        snumber: String,
        unit: String,
        name: String,
        uploadFileNames: List<String>
    ) {
        val key = database.child("posts").push().key
        val post = Post(
            userId,
            username,
            warehouse,
            area,
            row,
            slot,
            snumber,
            name,
            format,
            unit,
            number,
            count,
            remarks,
            uploadFileNames
        )
        val postValues = post.toMap()
        val childUpdates: MutableMap<String, Any?> = HashMap()
        childUpdates["/posts/$key"] = postValues
        childUpdates["/user-posts/$userId/$key"] = postValues
        database.updateChildren(childUpdates)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TAG = "NewPostFragment"
        private const val REQUIRED = "Required"
        private const val PICK_IMAGE_REQUEST = 1
    }
}