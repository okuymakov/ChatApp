package com.kuymakov.chat.ui.home

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.core.view.*
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.*
import com.kuymakov.chat.base.mvi.BaseMviView
import com.kuymakov.chat.base.ui.avatar.AvatarGenerator
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.databinding.FragmentNavDrawerBinding
import com.kuymakov.chat.databinding.NavHeaderBinding
import com.kuymakov.chat.domain.models.Chat
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.ui.home.adapter.ChatsAdapter
import com.kuymakov.chat.views.RecyclerStateLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment :
    BaseMviView<HomeStore.State, HomeStore.Action, HomeStore.Event>(R.layout.fragment_nav_drawer) {
    private val binding by viewBinding(FragmentNavDrawerBinding::bind)
    private val viewModel: HomeViewModel by viewModels()
    private val navController: NavController by lazy { findNavController() }
    private var adapter: ChatsAdapter? = null
    private var navHeader: NavHeaderBinding? = null

    override fun init() {
        bind(viewModel)
        viewModel.bind(this)
        navHeader = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
        setupInsets()
        setupToolbar()
        setupNavView()
        setupList()
        dispatch(HomeStore.Action.GetCurrentUser)
        dispatch(HomeStore.Action.GetChats)
        setupFragmentResultListener()
    }

    override fun onResume() {
        super.onResume()
        statusBarColor = Color.TRANSPARENT
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, binding.root).apply {
                isAppearanceLightStatusBars = false
            }
        }
    }

    override fun render(state: HomeStore.State) {
        val chats = state.chats
        when {
            state.isLoading -> {
                binding.content.chatsListState.updateState(RecyclerStateLayout.State.Loading)
            }
            state.hasError -> {
                binding.content.chatsListState.updateState(RecyclerStateLayout.State.Error)
            }
            state.hasNoInternet -> {}
            chats?.isEmpty() ?: false -> {
                binding.content.chatsListState.updateState(RecyclerStateLayout.State.Empty)
            }
            chats?.isNotEmpty() ?: false -> {
                binding.content.chatsListState.updateState(RecyclerStateLayout.State.Success)
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

    private fun setupInsets() {
        binding.content.appbarLayout.doOnApplyWindowInsets {
            addSystemTopPadding()
        }
        navHeader!!.root.doOnApplyWindowInsets {
            addSystemTopPadding()
        }

        binding.navView.doOnApplyWindowInsets {
            add { insets, paddings, _ ->
                navHeader!!.root.addSystemTopPadding(insets, paddings)
            }
        }
        binding.drawerLayout.doOnApplyWindowInsets {
            addSystemBottomPadding()
        }
    }


    private fun setupList() {
        adapter = ChatsAdapter(::onClick)
        binding.content.chatsList.adapter = adapter
    }

    private fun setupToolbar() {
        with(binding.content.toolbar) {
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

    private fun setupFragmentResultListener() {
        setFragmentResultListener("updateProfilePhoto") { _, bundle ->
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
}




