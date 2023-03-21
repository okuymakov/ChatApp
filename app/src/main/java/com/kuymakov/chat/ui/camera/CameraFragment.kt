package com.kuymakov.chat.ui.camera

import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.doOnApplyWindowInsets
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.databinding.FragmentCameraBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class CameraFragment : Fragment(R.layout.fragment_camera) {
    private val navController by lazy { findNavController() }
    private var imageCapture: ImageCapture? = null
    private val binding by viewBinding(FragmentCameraBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        startCamera()
        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, binding.root).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isNavigationBarContrastEnforced = false
                }
                isAppearanceLightNavigationBars = false
                hide(WindowInsetsCompat.Type.statusBars())
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, binding.root).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isNavigationBarContrastEnforced = true
                }
                isAppearanceLightNavigationBars = true
                show(WindowInsetsCompat.Type.statusBars())
                statusBarColor = context.getColor(R.color.blue)
                navigationBarColor = context.getColor(R.color.white_50)
            }
        }
    }

    private fun setupInsets() {
        binding.takePhotoButton.doOnApplyWindowInsets {
            addSystemBottomMargin()
        }
        binding.flipCameraButton.doOnApplyWindowInsets {
            addSystemBottomMargin()
        }
    }

    private fun startCamera() {
        imageCapture = ImageCapture.Builder().build()
        val context = requireContext()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (ex: Exception) {
                Timber.e(ex)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                val appName = resources.getString(R.string.app_name)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$appName")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()


        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object :
                ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = outputFileResults.savedUri ?: return
                    navigateToPhotoResult(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Timber.e(exception)
                }
            })
    }

    private fun navigateToPhotoResult(uri: Uri) {
        navController.navigate(
            CameraFragmentDirections.actionCameraFragmentToPhotoResultFragment(uri)
        )
    }
}