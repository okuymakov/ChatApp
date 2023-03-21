package com.kuymakov.chat.ui.chat

import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.*
import com.kuymakov.chat.base.mvi.BaseMviView
import com.kuymakov.chat.base.ui.avatar.AvatarGenerator
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.databinding.FragmentChatBinding
import com.kuymakov.chat.domain.models.GroupChat
import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.domain.models.PrivateChat
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.domain.network.request.MessageRequest
import com.kuymakov.chat.ui.chat.adapter.MessagesAdapter
import com.kuymakov.chat.views.RecyclerStateLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ChatFragment :
    BaseMviView<ChatStore.State, ChatStore.Action, ChatStore.Event>(R.layout.fragment_chat) {
    private val viewModel by viewModels<ChatViewModel>()
    private val args by navArgs<ChatFragmentArgs>()
    private val binding by viewBinding(FragmentChatBinding::bind)
    private val navController by lazy {
        findNavController()
    }
    private var _adapter: MessagesAdapter? = null
    private val adapter: MessagesAdapter get() = _adapter!!
    private var isActionMode = false
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }


    override fun init() {
        bind(viewModel)
        viewModel.bind(this)
        binding.scrollDownFab.hide()
        dispatch(ChatStore.Action.GetMessages(args.chat.id))
        //dispatch(ChatStore.Action.ListenMessagesUpdates(args.chat.id))
        setupInsets()
        setupToolbar()
        setupToolbarContent()
        setupChatInput()
        setupList()
    }


    override fun onEvent(event: ChatStore.Event) {
        when (event) {
            is ChatStore.Event.Navigation -> navController.navigate(event.directions)
        }
    }

    override fun render(state: ChatStore.State) {
        when {
            state.isLoading -> {
                binding.messagesListState.updateState(RecyclerStateLayout.State.Loading)
            }
            state.hasError -> {

            }
            state.hasNoInternet -> {}
            else -> {
                adapter.submitData(lifecycle, state.messages)
            }
        }
        val drawable = AppCompatResources.getDrawable(
            requireContext(),
            state.sendButtonResId
        )
        binding.sendMessageButton.setImageDrawable(drawable)

    }

    private fun onClick(msg: Message) {
        if (isActionMode) {
            launchOnLifecycle(state = Lifecycle.State.STARTED) {
                val newData =
                    adapter.snapshot().items.replace(msg, msg.copy(isSelected = !msg.isSelected))
                adapter.submitData(PagingData.from(newData))
                updateCounter()
            }
        }
    }

    private fun onLongClick(msg: Message) {
        if (!isActionMode) {
            actionMode = binding.toolbar.startActionMode(actionModeCallback)
        }
        launchOnLifecycle(state = Lifecycle.State.STARTED) {
            val newData =
                adapter.snapshot().items.replace(msg, msg.copy(isSelected = !msg.isSelected))
            adapter.submitData(PagingData.from(newData))
            updateCounter()
        }
    }

    private fun updateCounter() {
        actionMode?.let {
            val count = adapter.selected().size
            if (count > 0) {
                it.title = count.toString()
            } else it.finish()
        }
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode!!.menuInflater.inflate(R.menu.chat_toolbar_contextual_menu, menu)
            isActionMode = true
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }


        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = when (item?.itemId) {
            R.id.delete -> {
                dispatch(
                    ChatStore.Action.DeleteMessages(
                        args.chat.id,
                        adapter.selected().map { it.id })
                )
                actionMode?.finish()
                true
            }
            else -> false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            val selected = adapter.selected()
            val newData = adapter.snapshot().items.map {
                if (selected.contains(it)) (it as Message).copy(isSelected = false) else it
            }
            adapter.submitData(lifecycle, PagingData.from(newData))
            isActionMode = false
        }

    }

    override fun onDestroyView() {
        _adapter = null
        super.onDestroyView()
    }

    private fun setupInsets() {
        binding.appbarLayout.doOnApplyWindowInsets {
            addSystemTopPadding()
        }
        binding.inputLayout.doOnApplyWindowInsets {
            addSystemBottomPadding()
            fitIme()
        }
        binding.messagesList.doOnApplyWindowInsets {
            //addSystemBottomPadding()
        }
    }

    private fun setupChatInput() {
        with(binding) {
            messageInput.requestFocus()
            messageInput.addTextChangedListener {
                //dispatch(ChatStore.Action.ChangeMessageInputType)
            }

            sendMessageButton.setOnClickListener {
                val message = MessageRequest(
                    id = UUID.randomUUID().toString(),
                    chatId = args.chat.id,
                    text = messageInput.text.toString()
                )
                messageInput.text.clear()
                dispatch(ChatStore.Action.CreateMessage(message))
            }
        }

    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            showOverflowMenu()
            setNavigationOnClickListener { navController.popBackStack() }
        }
    }

    private fun setupToolbarContent() {
        val chat = args.chat
        var avatarLetters = ""
        when (chat) {
            is PrivateChat -> {
                avatarLetters = chat.username
                binding.title.text = chat.username
                binding.status.text = if (chat.status == User.Status.ONLINE) {
                    resources.getString(R.string.user_status_online)
                } else {
                    chat.lastSeen?.lastSeen(requireContext()) ?: "Last seen recently"
                }
            }
            is GroupChat -> {
                avatarLetters = chat.title
                binding.title.text = chat.title
            }
        }
        binding.avatar.load(chat.imageUrl) {
            error(AvatarGenerator(requireContext()).generate(avatarLetters))
        }
    }

    private fun setupList() {
        _adapter = MessagesAdapter(::onClick, ::onLongClick)
        binding.messagesList.adapter = adapter
        binding.scrollDownFab.setOnClickListener {
            binding.messagesList.smoothScrollToPosition(0)
        }
        binding.messagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val extent = binding.messagesList.computeVerticalScrollExtent()
                val range = binding.messagesList.computeVerticalScrollRange()
                val offset = binding.messagesList.computeVerticalScrollOffset()
                if (dy > 0 && range - offset - extent > extent) {
                    binding.scrollDownFab.show()
                } else {
                    binding.scrollDownFab.hide()
                }
            }

        })

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading) {
                if (adapter.itemCount < 1) {
                    binding.messagesListState.updateState(RecyclerStateLayout.State.Empty)
                } else {
                    binding.messagesListState.updateState(RecyclerStateLayout.State.Success)
                }
            }
        }
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.messagesList.scrollToPosition(0)
                }
            }
        })
    }
}