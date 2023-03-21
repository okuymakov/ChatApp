package com.kuymakov.chat.ui.search

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.getQueryText
import com.kuymakov.chat.base.extensions.hideKeyboard
import com.kuymakov.chat.base.extensions.launchOnLifecycle
import com.kuymakov.chat.base.extensions.showKeyboard
import com.kuymakov.chat.base.mvi.BaseMviView
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.databinding.FragmentSearchBinding
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.ui.search.adapter.UsersAdapter
import com.kuymakov.chat.views.RecyclerStateLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn

@FlowPreview
@AndroidEntryPoint
class SearchFragment :
    BaseMviView<SearchStore.State, SearchStore.Action, SearchStore.Event>(R.layout.fragment_search) {
    private val viewModel by viewModels<SearchViewModel>()
    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val navController by lazy { findNavController() }
    private var adapter: UsersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }


    override fun init() {
        bind(viewModel)
        viewModel.bind(this)
        setupToolbar()
        setupSearch()
        setupList()
    }

    override fun render(state: SearchStore.State) {
        when (state) {
            SearchStore.State.EmptyError -> binding.usersListState.updateState(RecyclerStateLayout.State.Error)
            SearchStore.State.Error -> {}
            SearchStore.State.Loading -> binding.usersListState.updateState(RecyclerStateLayout.State.Loading)
            SearchStore.State.NoInternetError -> binding.usersListState.updateState(
                RecyclerStateLayout.State.Error
            )
            SearchStore.State.Empty -> binding.usersListState.updateState(RecyclerStateLayout.State.Empty)
            is SearchStore.State.Success -> {
                binding.usersListState.updateState(RecyclerStateLayout.State.Success)
                adapter?.submitList(state.users)
            }
        }
    }

    override fun onEvent(event: SearchStore.Event) {
        when (event) {
            is SearchStore.Event.Navigation -> {
                val navOptions = NavOptions.Builder()
                    .setRestoreState(true)
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.chatFragment, inclusive = false, saveState = true).build()
                navController.navigate(event.directions, navOptions)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    private fun onClick(user: User) {
        dispatch(SearchStore.Action.UserItemClick(user.id))
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            showOverflowMenu()
            setNavigationOnClickListener {
                hideKeyboard()
                navController.popBackStack()
            }
        }
    }

    private fun setupList() {
        adapter = UsersAdapter(::onClick)
        binding.usersList.adapter = adapter
    }

    private fun setupSearch() {
        val searchView = binding.search
        searchView.requestFocus()
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            }
        }

        launchOnLifecycle(Lifecycle.State.STARTED) {
            searchView.getQueryText().debounce(300)
                .distinctUntilChanged()
                .filterNot { it.isBlank() }
                .flowOn(Dispatchers.Default)
                .collect { query ->
                    dispatch(SearchStore.Action.Search(query))
                }
        }
    }
}