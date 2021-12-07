package com.hyk.cameraorgallery

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.apache.commons.lang3.time.FastDateFormat
import java.io.File
import java.util.*

fun AppCompatActivity.createTempImageFile(fileName: String? = null): File {
    val mFolder = File("${baseContext.filesDir}/Images")

    if (!mFolder.exists()) {
        mFolder.mkdir()
    }

    val tempFile = File(
        mFolder.absolutePath,
        if (fileName.isNullOrBlank()) "IMG_${getTimeStampString()}.jpg" else fileName
    )
    tempFile.createNewFile()

    return tempFile
}

private val TIME_ZONE: TimeZone = TimeZone.getDefault()

fun getTimeStampString(): String = FastDateFormat.getInstance("yyyyMMdd_HHmmss", TIME_ZONE).format(
    Calendar.getInstance().time
)