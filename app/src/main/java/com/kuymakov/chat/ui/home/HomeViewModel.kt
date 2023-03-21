package com.kuymakov.chat.ui.home

import androidx.lifecycle.viewModelScope
import com.kuymakov.chat.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    storeFactory: HomeStore.Factory
) : MviViewModel<HomeStore.State, HomeStore.Action, HomeStore.Event, HomeStore>() {
    override val store = storeFactory.create(viewModelScope)
}