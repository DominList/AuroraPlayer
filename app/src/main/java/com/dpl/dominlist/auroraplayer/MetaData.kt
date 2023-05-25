package com.dpl.dominlist.auroraplayer

import android.app.Application
import android.net.Uri
import android.provider.MediaStore

data class MetaData(
    val fileName: String
)

interface MetaDataReader {
    fun getMetaDataFromUri(uri: Uri): MetaData?
}

class MediaDataReader(
    private val app: Application
) : MetaDataReader {
    override fun getMetaDataFromUri(uri: Uri): MetaData? {
        if (uri.scheme != "content") {
            return null
        }
        val fileName = app.contentResolver.query(
            uri,
            arrayOf(MediaStore.Video.VideoColumns.DISPLAY_NAME),
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(index)
        }
        return fileName?.let { path ->
            MetaData(
                fileName = Uri.parse(path).lastPathSegment ?: return null
            )
        }
    }
}