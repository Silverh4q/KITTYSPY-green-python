package com.example

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val sourceDir: String,
    val isSystem: Boolean
)
