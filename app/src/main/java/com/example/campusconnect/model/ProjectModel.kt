package com.example.campusconnect.model

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class ProjectModel(
    val uid:String?=null,
    val projectname:String?=null,
    val projecturl:String?=null
)
