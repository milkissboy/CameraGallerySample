package com.hyk.cameraorgallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.constraintlayout.utils.widget.ImageFilterView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.hyk.cameraorgallery.Logger.d
import com.hyk.cameraorgallery.Logger.i

class MainActivity : AppCompatActivity(), CoroutineScope {

    // 비동기 처리
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var currentImageUri:Uri

    var imageView: ImageFilterView? = null
    var seekWarmth: AppCompatSeekBar? = null
    var seekContrast: AppCompatSeekBar? = null
    var seekSaturation: AppCompatSeekBar? = null
    var load: Button? = null
    var loadGallery: Button? = null

    /** 카메라 관련 */
    private val permissionsCamera = arrayOf(
        Manifest.permission.CAMERA
    )

    private val requestPermissionCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            for (entry in it.entries) {
                if (!entry.value) {
                    showPermissionSnackBar(window.decorView, R.string.permission_read)
                    return@registerForActivityResult
                }
            }
            dispatchTakePictureIntent()
        }

    private fun dispatchTakePictureIntent() {
        val photoUri: Uri? = createTempImageFile().let { file ->
            FileProvider.getUriForFile(
                baseContext,
                baseContext.applicationContext.packageName.toString() + ".fileProvider",
                file
            )
        }
        photoUri?.let {
            currentImageUri = it
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri)
            }.also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    getImageFromCapture.launch(takePictureIntent)
                }
            }
        }
        i("load photoUri $photoUri")
    }

    private val getImageFromCapture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            d("bbb ${it.resultCode} photoUri $currentImageUri")
            if (it.resultCode == Activity.RESULT_OK) {
                //val imageBitmap = it.extras.get("data") as Bitmap
                val bitmap = BitmapFactory.decodeStream(contentResolver?.openInputStream(currentImageUri))
                imageView?.setImageBitmap(bitmap)
            }
        }

    /** 갤러리 관련 */
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val requestPermissionGallery =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            for (entry in it.entries) {
                if (!entry.value) {
                    showPermissionSnackBar(window.decorView, R.string.permission_read)
                    return@registerForActivityResult
                }
            }

            getImageFromGallery.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.also {
                    launch(Dispatchers.Main) {
                        val file = baseContext.getBitmap(it.data)?.compress(createTempImageFile())
                        if (file != null) {
                            if (file.exists()) {
                                val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                                imageView?.setImageBitmap(myBitmap)
                            }

                        } else {
                            showToast("이미지를 찾을 수 없습니다.")
                        }
                    }
                }
            }
        }

    // 비동기 종료
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupLayout()

        setupListener()

        seekWarmth?.progress = 50
        seekContrast?.progress = 50
        seekSaturation?.progress = 50
    }

    private fun setupLayout() {
        imageView = findViewById(R.id.imageView)
        seekWarmth = findViewById(R.id.seek_warmth)
        seekContrast = findViewById(R.id.seek_contrast)
        seekSaturation = findViewById(R.id.seek_saturation)
        load = findViewById(R.id.camera)
        loadGallery = findViewById(R.id.gallery)
    }

    private fun setupListener() {
        load?.setOnClickListener {
            requestPermissionCamera.launch(permissionsCamera)
        }

        loadGallery?.setOnClickListener {
            requestPermissionGallery.launch(permissions)
        }

        seekWarmth?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
                val percentage = (progress / 100.0f)
                imageView?.warmth = (percentage) + 1
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
            }
        })

        seekContrast?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
                val percentage = (progress / 100.0f)
                imageView?.contrast = (percentage) + 1
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
            }
        })


        seekSaturation?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {
                val percentage = (progress / 100.0f)
                imageView?.saturation = (percentage) + 1
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
            }
        })
    }
}