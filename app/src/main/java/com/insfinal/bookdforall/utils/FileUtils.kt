package com.insfinal.bookdforall.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

object FileUtils {
    fun fromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "temp_file"
        val tempFile = File.createTempFile(fileName, null, context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        return tempFile
    }
}