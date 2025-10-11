package com.google.firebase.quickstart.database.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Template(
    var uid: String? = null,
    var name: String? = null,
    var fields: List<Field>? = null
)

@IgnoreExtraProperties
data class Field(
    var name: String? = null,
    var type: String? = null // e.g., "text", "number", "boolean"
)