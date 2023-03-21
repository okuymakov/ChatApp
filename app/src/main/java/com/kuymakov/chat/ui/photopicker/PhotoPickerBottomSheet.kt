package com.kuymakov.chat.ui.photopicker

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.launchOnLifecycle
import com.kuymakov.chat.databinding.DialogPhotoPickerBinding
import com.kuymakov.chat.domain.models.Photo
import com.kuymakov.chat.ui.photopicker.adapter.PhotosAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class PhotoPickerBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: DialogPhotoPickerBinding
    private val viewModel by viewModels<PhotoPickerViewModel>()
    private val navController by lazy { findNavController() }
    private val adapter by lazy { PhotosAdapter(::onClick) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPhotoPickerBinding.bind(
            inflater.inflate(
                R.layout.dialog_photo_picker,
                container
            )
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        binding.closeBtn.setOnClickListener { dismiss() }
        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.photos.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.photosList.adapter = null
    }

    private fun setupList() {
        val list = binding.photosList
        list.adapter = adapter
        val dialog = dialog ?: return
        val bottomSheet =
            dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        val behavior = BottomSheetBehavior.from(bottomSheet)
        list.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_UP -> {
                        behavior.isDraggable = true
                    }
                    MotionEvent.ACTION_DOWN -> {
                        behavior.isDraggable = false
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun onClick(photo: Photo) {
        navigateToPhotoResult(photo.uri)
    }

    private fun navigateToPhotoResult(uri: Uri) {
        navController.navigate(
            PhotoPickerBottomSheetDirections.actionPhotoPickerBottomSheetToPhotoResultFragment(uri)
        )
    }

}