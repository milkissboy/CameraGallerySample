package com.hyk.cameraorgallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.*

fun Context.getBitmap(uri: Uri?): Bitmap? {
    var bitmap: Bitmap? = null

    uri?.apply {
        contentResolver.openInputStream(this)?.use { inputStream ->
            BufferedInputStream(inputStream).use { bitmap = BitmapFactory.decodeStream(it) }
        }

        contentResolver.openInputStream(this)?.use { inputStream ->
            BufferedInputStream(inputStream).use {
                val degrees = it.getExifInterface().getOrientation()
                bitmap = bitmap?.rotate(degrees.toFloat())
            }
        }
    }

    return bitmap
}

fun Bitmap.compress(tempFile: File): File? {
    try {
        FileOutputStream(tempFile).use {
            this.compress(Bitmap.CompressFormat.JPEG, 70, it)
        }
    } catch (ex: FileNotFoundException) {
        return null
    }

    this.recycle()

    if (tempFile.length() <= 2097152) {
        return tempFile
    } else {
        compress(tempFile)
    }

    return null
}

private fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.setRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

private fun InputStream.getExifInterface(): ExifInterface {
    val exifInterface: ExifInterface

    BufferedInputStream(this).use {
        exifInterface = ExifInterface(it)
    }

    return exifInterface
}

private fun ExifInterface.getOrientation(): Int =
    when (this.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }