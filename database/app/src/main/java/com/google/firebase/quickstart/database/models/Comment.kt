package com.google.firebase.quickstart.database.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    var uid: String? = null,
    var author: String? = null,
    var text: String? = null
)