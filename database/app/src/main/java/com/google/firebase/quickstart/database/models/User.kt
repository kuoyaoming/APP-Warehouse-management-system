package com.google.firebase.quickstart.database.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var username: String? = null,
    var email: String? = null
)