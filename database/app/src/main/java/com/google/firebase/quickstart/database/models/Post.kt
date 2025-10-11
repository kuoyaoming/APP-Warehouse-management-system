package com.google.firebase.quickstart.database.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    var uid: String? = null,
    var author: String? = null,
    var warehouse: String? = null,
    var area: String? = null,
    var row: String? = null,
    var slot: String? = null,
    var snumber: String? = null,
    var name: String? = null,
    var format: String? = null,
    var unit: String? = null,
    var number: String? = null,
    var count: String? = null,
    var remarks: String? = null,
    var uploadFileNames: List<String>? = null,
    var starCount: Int = 0,
    var stars: MutableMap<String, Boolean> = HashMap()
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "author" to author,
            "warehouse" to warehouse,
            "area" to area,
            "row" to row,
            "slot" to slot,
            "snumber" to snumber,
            "name" to name,
            "format" to format,
            "unit" to unit,
            "number" to number,
            "count" to count,
            "remarks" to remarks,
            "uploadFileNames" to uploadFileNames,
            "starCount" to starCount,
            "stars" to stars
        )
    }
}