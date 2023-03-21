package com.kuymakov.chat.ui.search

import androidx.navigation.NavDirections
import com.kuymakov.chat.base.mvi.DefaultStore
import com.kuymakov.chat.base.mvi.Store
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.repositories.ChatRepository
import com.kuymakov.chat.domain.repositories.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class SearchStore @AssistedInject constructor(
    private val repo: UserRepository,
    private val chatRepo: ChatRepository,
    @Assisted coroutineScope: CoroutineScope
) : Store<SearchStore.State, SearchStore.Action, SearchStore.Event> by DefaultStore(
    initialState = State.Empty,
    scope = coroutineScope,
    onAction = { action ->
        when (action) {
            is Action.Search -> {
                reduce { State.Loading }
                execute {
                    when (val res = repo.fetchUsers(action.query)) {
                        is Success -> {
                            if (res.data.isNotEmpty()) reduce { State.Success(res.data) }
                            else reduce { State.Empty }
                        }
                        else -> {
                            if (state is State.Empty) {
                                reduce { State.EmptyError }
                            }
                            reduce { State.Error }
                        }
                    }
                }
            }
            is Action.UserItemClick -> execute {
                val res = chatRepo.getOrCreatePrivateChat(action.userId)
                if (res is Success) publish(
                    Event.Navigation(SearchFragmentDirections.actionSearchFragmentToChatFragment(res.data))
                )
            }
        }
    }
) {

    sealed class State {
        data class Success(val users: List<User>) : State()
        object Empty : State()
        object Loading : State()
        object EmptyError : State()
        object Error : State()
        object NoInternetError : State()
    }

    sealed class Action {
        data class UserItemClick(val userId: String) : Action()
        data class Search(val query: String) : Action()
    }

    sealed class Event {
        data class Navigation(val directions: NavDirections) : Event()
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted coroutineScope: CoroutineScope): SearchStore
    }

}