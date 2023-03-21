package com.kuymakov.chat.ui.photoresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.load
import com.kuymakov.chat.databinding.FragmentPhotoResultBinding


class PhotoResultFragment : DialogFragment() {
   lateinit var binding: FragmentPhotoResultBinding

    private val args by navArgs<PhotoResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.Theme_Chat_Dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photo.load(args.uri)
    }
}