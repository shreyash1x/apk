package com.ncorti.kotlin.template.app.data

import java.io.File

data class Recording(
    val file: File,
    val phoneNumber: String,
    val timestamp: String
)
