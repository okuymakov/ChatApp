package com.kuymakov.chat.ui.home

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import com.kuymakov.chat.databinding.DialogPhotoSourceChooserBinding

class PhotoSourceChooser : DialogFragment() {

    private lateinit var binding: DialogPhotoSourceChooserBinding

    private val navController by lazy {
        findNavController()
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                navigateToPhotoPicker()
            } else {
                AlertDialog.Builder(requireActivity())
                    .setMessage("You need grant storage permissions for usage first")
                    .create().show()
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                navigateToCamera()
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPhotoSourceChooserBinding.inflate(requireActivity().layoutInflater)
        binding.cameraBtn.setOnClickListener { requestCameraPermissions() }
        binding.galleryBtn.setOnClickListener { requestStoragePermissions() }
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle(getString(R.string.title_choose_photo_source))
            .create()
    }

    private fun requestStoragePermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                navigateToPhotoPicker()
            }
            else -> {
                requestStoragePermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun requestCameraPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateToCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireActivity())
                    .setMessage("You need grant camera permissions for usage first")
                    .create().show()
            }
            else -> {
                requestCameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    private fun navigateToCamera() {
        navController.navigate(PhotoSourceChooserDirections.actionPhotoSourceChooserToCameraFragment())
    }

    private fun navigateToPhotoPicker() {
        navController.navigate(PhotoSourceChooserDirections.actionPhotoSourceChooserToPhotoPickerBottomSheet())
    }
}

