package com.kuymakov.chat.base.extensions

import androidx.fragment.app.Fragment

fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard() }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard() }
}


//var Fragment.currentUser: User
//    get() {
//        val sharedPref =
//            requireContext().getSharedPreferences(Constants.USER_STORAGE_NAME, Context.MODE_PRIVATE)
//        return with(sharedPref) {
//            User(
//                id = getString("userId", null)!!,
//                username = getString("username", null)!!,
//                imageUrl = getString("imageUrl", null),
//                email = getString("email", null),
//            )
//        }
//    }
//
//    set(value) {
//        val sharedPref =
//            requireContext().getSharedPreferences(Constants.USER_STORAGE_NAME, Context.MODE_PRIVATE)
//        with(sharedPref.edit()) {
//            putString("userId", value.id)
//            putString("username", value.username)
//            putString("imageUrl", value.imageUrl)
//            putString("email", value.email)
//            apply()
//        }
//    }