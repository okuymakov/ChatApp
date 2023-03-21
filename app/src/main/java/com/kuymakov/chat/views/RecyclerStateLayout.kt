package com.kuymakov.chat.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.kuymakov.chat.R
import com.kuymakov.chat.databinding.RecycleStateLayoutBinding

class RecyclerStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding: RecycleStateLayoutBinding
    private var rv: RecyclerView? = null

    private var state: State? = null

    enum class State {
        Loading, Error, Empty, Success
    }


    var errorText: String = ""
        set(value) {
            field = value
            binding.errorMessage.text = value
        }

    var emptyText: String = ""
        set(value) {
            field = value
            binding.emptyMessage.text = value
        }

    val progressBarEnabled: Boolean

    @DrawableRes
    var errorIcon = 0
        set(value) {
            field = value
            binding.errorImg.setImageResource(value)
        }

    @DrawableRes
    var emptyIcon = 0
        set(value) {
            field = value
            binding.emptyImg.setImageResource(value)
        }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.recycle_state_layout, this, true)
        binding = RecycleStateLayoutBinding.bind(this)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RecyclerStateLayout,
            0,
            0
        ).apply {
            try {
                errorText = getString(R.styleable.RecyclerStateLayout_errorText) ?: ""
                emptyText = getString(R.styleable.RecyclerStateLayout_emptyText) ?: ""
                errorIcon = getResourceId(R.styleable.RecyclerStateLayout_errorIcon, 0)
                emptyIcon = getResourceId(R.styleable.RecyclerStateLayout_emptyIcon, 0)
                progressBarEnabled =
                    getBoolean(R.styleable.RecyclerStateLayout_progressBarEnabled, false)

                val loadingLayout =
                    getResourceId(R.styleable.RecyclerStateLayout_loadingLayout, 0)
                if (loadingLayout != 0) {
                    val linearLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams =
                            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                        for (i: Int in 1..10) {
                            val loadingItem =
                                LayoutInflater.from(context).inflate(loadingLayout, null)
                            addView(loadingItem)
                        }
                    }
                    binding.loadingView.addView(linearLayout)
                }
            } finally {
                recycle()
            }
        }
        render()
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child is RecyclerView) {
            rv = child
        }
    }

    private fun render() {
        binding.loadingView.visibility =
            if (state == State.Loading && !progressBarEnabled) View.VISIBLE else View.GONE
        binding.loadingSpinner.visibility =
            if (state == State.Loading && progressBarEnabled) View.VISIBLE else View.GONE
        binding.errorView.visibility = if (state == State.Error) View.VISIBLE else View.GONE
        binding.emptyView.visibility = if (state == State.Empty) View.VISIBLE else View.GONE
        rv?.visibility = if (state == State.Success) View.VISIBLE else View.GONE
    }

    fun setOnRetryClickListener(callback: () -> Unit) {
        binding.retryBtn.setOnClickListener {
            callback()
        }
    }

    fun updateState(state: State) {
        this.state = state
        render()
    }
}
