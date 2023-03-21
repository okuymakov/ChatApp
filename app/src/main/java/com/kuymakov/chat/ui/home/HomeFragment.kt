package com.kuymakov.chat.ui.home

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.doOnApplyWindowInsets
import com.kuymakov.chat.base.extensions.load
import com.kuymakov.chat.base.extensions.toByteArray
import com.kuymakov.chat.base.mvi.BaseMviView
import com.kuymakov.chat.base.ui.avatar.AvatarGenerator
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.databinding.FragmentHomeBinding
import com.kuymakov.chat.databinding.NavHeaderBinding
import com.kuymakov.chat.domain.models.Chat
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.ui.home.adapter.ChatsAdapter
import com.kuymakov.chat.views.RecyclerStateLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment :
    BaseMviView<HomeStore.State, HomeStore.Action, HomeStore.Event>(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()
    private val navController: NavController by lazy { findNavController() }
    private var adapter: ChatsAdapter? = null
    private var navHeader: NavHeaderBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }

    private fun setupInsets() {
        binding.appbarLayout.doOnApplyWindowInsets {
            addSystemTopPadding()
        }
        navHeader?.root?.doOnApplyWindowInsets {
            addSystemTopPadding()
        }
        binding.drawerLayout.doOnApplyWindowInsets {
            addSystemBottomPadding()
        }
    }


    override fun init() {
        bind(viewModel)
        viewModel.bind(this)
        navHeader = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, binding.root).apply {
                statusBarColor = Color.TRANSPARENT
            }
        }
        setupInsets()
        setupToolbar()
        setupNavView()
        setupList()
        dispatch(HomeStore.Action.GetCurrentUser)
        dispatch(HomeStore.Action.GetChats)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, binding.root).apply {
                statusBarColor = context.getColor(R.color.blue)
            }
        }
    }

    fun updateProfilePhoto() {
        setFragmentResultListener("requestKey") { key, bundle ->
            val uri = bundle.getString("photoUri")?.toUri() ?: return@setFragmentResultListener
            val parcelFileDescriptor: ParcelFileDescriptor? =
                requireActivity().contentResolver.openFileDescriptor(uri, "r")
            parcelFileDescriptor?.use {
                val fd = it.fileDescriptor
                val bitmap = BitmapFactory.decodeFileDescriptor(fd).toByteArray()
                dispatch(HomeStore.Action.UpdateProfilePhoto(bitmap))
            }
        }
    }

    override fun render(state: HomeStore.State) {
        val chats = state.chats
        when {
            state.isLoading -> {
                binding.chatsListState.updateState(RecyclerStateLayout.State.Loading)
            }
            state.hasError -> {
                binding.chatsListState.updateState(RecyclerStateLayout.State.Error)
            }
            state.hasNoInternet -> {}
            chats?.isEmpty() ?: false -> {
                binding.chatsListState.updateState(RecyclerStateLayout.State.Empty)
            }
            chats?.isNotEmpty() ?: false -> {
                binding.chatsListState.updateState(RecyclerStateLayout.State.Success)
                adapter?.submitList(chats)
            }
        }
        val user = state.user
        if (user != null) {
            setupNavViewHeader(state.user)
        }
    }

    override fun onEvent(event: HomeStore.Event) {
        when (event) {
            is HomeStore.Event.Navigation -> navController.navigate(event.directions)
            is HomeStore.Event.OpenDrawerLayout -> {
                if (event.open) binding.drawerLayout.open() else binding.drawerLayout.close()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        navHeader = null
    }

    private fun onClick(chat: Chat) {
        dispatch(HomeStore.Action.ChatItemClick(chat))
    }

    private fun setupList() {
        adapter = ChatsAdapter(::onClick)
        binding.chatsList.adapter = adapter
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            showOverflowMenu()
            setNavigationOnClickListener {
                dispatch(HomeStore.Action.NavigationIconClick)
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.search -> {
                        dispatch(HomeStore.Action.SearchIconClick)
                        true
                    }
                    else -> false
                }
            }
        }
    }


    private fun setupNavView() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.setPhoto -> {
                    dispatch(HomeStore.Action.LoadPhoto)
                }
                R.id.logout -> {
                    dispatch(HomeStore.Action.Logout)
                }
                R.id.deleteAccount -> {
                    dispatch(HomeStore.Action.DeleteAccount)
                }
            }
            dispatch(HomeStore.Action.CloseDrawerLayout)
            false
        }
    }

    private fun setupNavViewHeader(user: User) {
        navHeader?.run {
            username.text = user.username
            email.text = user.email
            avatar.load(user.imageUrl) {
                placeholder(AvatarGenerator(requireContext()).generate(user.username))
            }
        }
    }
}




