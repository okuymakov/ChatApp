package com.kuymakov.chat.ui.chat

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.doOnApplyWindowInsets
import com.kuymakov.chat.base.extensions.drawable
import com.kuymakov.chat.base.extensions.lastSeen
import com.kuymakov.chat.base.extensions.load
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

    override fun init() {
        bind(viewModel)
        viewModel.bind(this)
        binding.scrollDownFab.hide()
        dispatch(ChatStore.Action.GetMessages(args.chat.id))
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
                binding.messagesListState.updateState(RecyclerStateLayout.State.Success)
                adapter.submitList(state.messages) {
                    if (isActionMode) {
                        updateCounter(state.messages.filter { it is Message && it.isSelected }.size)
                    }
                }
            }
        }
        binding.sendMessageButton.setImageDrawable(requireContext().drawable(state.sendButtonResId))

    }

    private fun onClick(msg: Message) {
        if (isActionMode) {
            dispatch(ChatStore.Action.SelectMessages(listOf(msg.id)))
        }
    }

    private fun onLongClick(msg: Message) {
        if (!isActionMode) {
            actionMode = binding.toolbar.startActionMode(actionModeCallback)
        }
        dispatch(ChatStore.Action.SelectMessages(listOf(msg.id)))
    }

    private fun updateCounter(count: Int) {
        actionMode?.let {
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
                dispatch(ChatStore.Action.DeleteMessages(args.chat.id, adapter.selected))
                actionMode?.finish()
                true
            }
            else -> false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            dispatch(ChatStore.Action.SelectMessages(adapter.selected))
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
    }

    private fun setupChatInput() {
        with(binding) {
            messageInput.requestFocus()
            messageInput.addTextChangedListener {
                if(messageInput.text.isBlank()) {
                    dispatch(ChatStore.Action.ChangeMessageInput(true))
                } else  dispatch(ChatStore.Action.ChangeMessageInput(false))
            }

            sendMessageButton.setOnClickListener {
                if(messageInput.text.isNotBlank()) {
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

        adapter.registerAdapterDataObserver(@ExperimentalBadgeUtils object : RecyclerView.AdapterDataObserver() {
            var count = adapter.itemCount
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                val isFromMe = (adapter.currentList[positionStart] as Message).isFromMe
                val pos =
                    (binding.messagesList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (isFromMe || pos == positionStart) {
                    binding.messagesList.scrollToPosition(0)
                    val badge = BadgeDrawable.create(requireActivity())
                    badge.number = itemCount - count
                    BadgeUtils.attachBadgeDrawable(badge,binding.scrollDownFab)
                    count = itemCount
                } else {
                    val badge = BadgeDrawable.create(requireActivity())
                    badge.number = itemCount - count
                    BadgeUtils.attachBadgeDrawable(badge,binding.scrollDownFab)
                    count = itemCount
                }
            }
        })
    }
}