package com.kuymakov.chat.ui.home

import androidx.navigation.NavDirections
import com.kuymakov.chat.base.mvi.*
import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.network.request.UserRequest
import com.kuymakov.chat.domain.usecases.GetChatsUseCase
import com.kuymakov.chat.domain.usecases.GetCurrentUserUseCase
import com.kuymakov.chat.domain.models.Chat
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.domain.repositories.AuthRepository
import com.kuymakov.chat.domain.repositories.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class HomeStore @AssistedInject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getChatsWithLastMessage: GetChatsUseCase,
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    @Assisted coroutineScope: CoroutineScope
) : Store<HomeStore.State, HomeStore.Action, HomeStore.Event> by DefaultStore(
    initialState = State(isLoading = true),
    scope = coroutineScope,
    onAction = { action ->
        when (action) {
            is Action.ChatItemClick -> publish(
                Event.Navigation(HomeFragmentDirections.actionHomeFragmentToChatFragment(action.chat))
            )
            Action.SearchIconClick -> publish(Event.Navigation(HomeFragmentDirections.actionHomeFragmentToSearchFragment()))
            Action.NavigationIconClick -> publish(Event.OpenDrawerLayout(true))
            Action.CloseDrawerLayout -> publish(Event.OpenDrawerLayout(false))
            Action.LoadPhoto -> publish(Event.Navigation(HomeFragmentDirections.actionHomeFragmentToPhotoSourceChooser()))
            is Action.GetChats -> execute {
                getChatsWithLastMessage().collect { value ->
                    when (value) {
                        is Success -> reduce {
                            copy(chats = value.data, isLoading = false, hasError = false)
                        }
                        is Failure -> reduce { copy(hasError = true, isLoading = false) }
                    }
                }
            }
            Action.GetCurrentUser -> execute {
                val res = getCurrentUser()
                if (res is Success) reduce { copy(user = res.data) }
            }
            Action.Logout -> execute {
                val res = authRepo.logout()
                if (res is Success) publish(Event.Navigation(HomeFragmentDirections.actionChatFragmentToLoginFragment()))
            }
            Action.DeleteAccount -> execute {
                val res = authRepo.deleteAccount()
                if (res is Success) publish(Event.Navigation(HomeFragmentDirections.actionChatFragmentToLoginFragment()))
            }
            is Action.UpdateProfilePhoto -> execute {
                val curUser = state.user
                    ?: getCurrentUser().let { if (it is Success) it.data else null }
                    ?: return@execute
                val res = userRepo.updateUser(
                    UserRequest(
                        id = curUser.id,
                        username = curUser.username,
                        email = curUser.email,
                        image = action.photo
                    )
                )
                if (res is Success) {
                    val userRes = getCurrentUser()
                    if (userRes is Success) {
                        reduce { copy(user = userRes.data) }
                    }
                }
            }
        }
    }
) {
    data class State(
        val chats: List<Chat>? = null,
        val isLoading: Boolean = false,
        val hasNoInternet: Boolean = false,
        val hasError: Boolean = false,
        val user: User? = null,
    )

    sealed class Action {
        object NavigationIconClick : Action()
        object SearchIconClick : Action()
        data class ChatItemClick(val chat: Chat) : Action()
        object CloseDrawerLayout : Action()
        object LoadPhoto : Action()
        object Logout : Action()
        object DeleteAccount : Action()
        object GetChats : Action()
        object GetCurrentUser : Action()
        class UpdateProfilePhoto(val photo: ByteArray) : Action()
    }

    sealed class Event {
        data class Navigation(val directions: NavDirections) : Event()
        data class OpenDrawerLayout(val open: Boolean) : Event()
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted coroutineScope: CoroutineScope): HomeStore
    }
}
