package com.google.firebase.quickstart.database

import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

open class BaseFragment : Fragment() {
    private var mProgressBar: ProgressBar? = null

    fun setProgressBar(resId: Int) {
        mProgressBar = view?.findViewById(resId)
    }

    fun showProgressBar() {
        mProgressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        mProgressBar?.visibility = View.INVISIBLE
    }

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid
}