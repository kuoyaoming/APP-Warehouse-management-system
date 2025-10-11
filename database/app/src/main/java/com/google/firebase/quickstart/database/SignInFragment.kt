package com.google.firebase.quickstart.database

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.quickstart.database.databinding.FragmentSignInBinding
import com.google.firebase.quickstart.database.models.User

class SignInFragment : BaseFragment(), View.OnClickListener {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var binding: FragmentSignInBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        setProgressBar(R.id.progressBar)

        binding!!.buttonSignIn.setOnClickListener(this)
        binding!!.buttonSignUp.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            onAuthSuccess(auth.currentUser)
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        if (!validateForm()) {
            return
        }
        showProgressBar()
        val email = binding!!.fieldEmail.text.toString()
        val password = binding!!.fieldPassword.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful)
                hideProgressBar()
                if (task.isSuccessful) {
                    onAuthSuccess(task.result.user)
                } else {
                    Toast.makeText(
                        context, "Sign In Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signUp() {
        Log.d(TAG, "signUp")
        if (!validateForm()) {
            return
        }
        showProgressBar()
        val email = binding!!.fieldEmail.text.toString()
        val password = binding!!.fieldPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                Log.d(TAG, "createUser:onComplete:" + task.isSuccessful)
                hideProgressBar()
                if (task.isSuccessful) {
                    onAuthSuccess(task.result.user)
                } else {
                    Toast.makeText(
                        context, "Sign Up Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun onAuthSuccess(user: FirebaseUser?) {
        val username = usernameFromEmail(user!!.email)
        writeNewUser(user.uid, username, user.email)
        findNavController().navigate(R.id.action_SignInFragment_to_MainFragment)
    }

    private fun usernameFromEmail(email: String?): String {
        return if (email!!.contains("@")) {
            email.split("@").toTypedArray()[0]
        } else {
            email
        }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(binding!!.fieldEmail.text.toString())) {
            binding!!.fieldEmail.error = "Required"
            result = false
        } else {
            binding!!.fieldEmail.error = null
        }
        if (TextUtils.isEmpty(binding!!.fieldPassword.text.toString())) {
            binding!!.fieldPassword.error = "Required"
            result = false
        } else {
            binding!!.fieldPassword.error = null
        }
        return result
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        database.child("users").child(userId).setValue(user)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.buttonSignIn) {
            signIn()
        } else if (i == R.id.buttonSignUp) {
            signUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val TAG = "SignInFragment"
    }
}