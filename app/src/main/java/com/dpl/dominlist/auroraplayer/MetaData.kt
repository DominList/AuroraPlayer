package com.dpl.dominlist.auroraplayer

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import java.util.concurrent.TimeUnit

data class MetaData(
    val uri: Uri?,
    val songName: String,
    val path: String,
    val albumName: String,
    val albumId: Int
)

interface MetaDataReader {
    fun getMetaDataFromUri(uri: Uri): MetaData?

    fun getAudioUris(): List<Uri>
}

class MediaDataReader(
    private val app: Application
) : MetaDataReader {
    override fun getMetaDataFromUri(uri: Uri): MetaData? {
        if (uri.scheme != "content") {
            return null
        }
        return app.contentResolver.query(
            uri,
            null,
            null,
            null
        )?.use { cursor ->
            cursor.moveToFirst()
            val songName: String =
                cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    .let { cursor.getString(it) }

            val path: String = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                .let { cursor.getString(it) }

            val albumName: String = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                .let { cursor.getString(it) }

            val albumId: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                .let { cursor.getInt(it) }

            MetaData(
                uri = uri,
                // TODO:
                songName = songName,
                path = path,
                albumName = albumName,
                albumId = albumId
            )
        }
    }


    override fun getAudioUris(): List<Uri> {
        val songsList = ArrayList<Uri>()
        val uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_INTERNAL)
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Video.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS).toString()
        )
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"


        app.contentResolver
            .query(uri, projection, null, null, sortOrder)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val id:Long = cursor.getColumnIndex(MediaStore.Audio.Media._ID).let {
                            cursor.getLong(it)
                        }
                        val contentUri: Uri =
                            ContentUris.withAppendedId(uri, id)
                        songsList.add(contentUri)
                    } while (cursor.moveToNext())
                }
            }
        return songsList
    }

    private fun getMusic() {}
}