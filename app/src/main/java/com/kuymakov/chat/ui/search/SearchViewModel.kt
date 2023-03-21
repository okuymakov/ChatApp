package com.kuymakov.chat.ui.search

import androidx.lifecycle.viewModelScope
import com.kuymakov.chat.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(storeFactory: SearchStore.Factory) :
    MviViewModel<SearchStore.State, SearchStore.Action, SearchStore.Event, SearchStore>() {
    override val store = storeFactory.create(viewModelScope)
}
