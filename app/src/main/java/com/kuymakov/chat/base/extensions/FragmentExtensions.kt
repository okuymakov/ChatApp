package com.kuymakov.chat.base.extensions

import androidx.fragment.app.Fragment

fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard() }
}

var Fragment.statusBarColor
    get() = requireActivity().window.statusBarColor
    set(value) {
        requireActivity().window.statusBarColor = value
    }

