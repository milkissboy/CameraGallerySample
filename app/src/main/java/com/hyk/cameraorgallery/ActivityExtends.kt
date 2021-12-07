package com.hyk.cameraorgallery

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

fun AppCompatActivity.showPermissionSnackBar(_view: View?, @StringRes id: Int) {
    _view?.let { view ->
        Snackbar.make(view, applicationContext.getString(id), Snackbar.LENGTH_SHORT).apply {
            setAction("확인") {
                applicationContext.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + applicationContext.packageName)
                    ).apply {
                        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
        }.show()
    }
}

fun AppCompatActivity.showToast(message: String?) {
    baseContext?.let {
        if (!TextUtils.isEmpty(message))
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
    }
}