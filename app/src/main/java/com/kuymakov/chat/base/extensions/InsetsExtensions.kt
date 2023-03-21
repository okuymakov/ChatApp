package com.kuymakov.chat.base.extensions

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.*

fun View.addSystemTopPadding(
    insets: WindowInsetsCompat,
    paddings: Rect,
    margins: Rect,
): WindowInsetsCompat {
    val initialPaddingTop = paddings.top
    val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
    updatePadding(top = initialPaddingTop + statusBarInsets.top)
    return insets
}

fun View.addSystemTopMargin(
    insets: WindowInsetsCompat,
    paddings: Rect,
    margins: Rect,
): WindowInsetsCompat {
    val initialMarginTop = margins.top
    val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
    updateLayoutParams<MarginLayoutParams> { updateMargins(top = initialMarginTop + statusBarInsets.top) }
    return insets
}

fun View.addSystemBottomPadding(
    insets: WindowInsetsCompat,
    paddings: Rect,
    margins: Rect,
): WindowInsetsCompat {
    val initialPaddingBottom = paddings.bottom
    val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
    updatePadding(bottom = initialPaddingBottom + navigationBarInsets.bottom)
    return insets
}

fun View.addSystemBottomMargin(
    insets: WindowInsetsCompat,
    paddings: Rect,
    margins: Rect,
): WindowInsetsCompat {
    val initialMarginBottom = margins.bottom
    val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
    updateLayoutParams<MarginLayoutParams> { updateMargins(bottom = initialMarginBottom + navigationBarInsets.bottom) }
    return insets
}

fun View.fitIme(
    insets: WindowInsetsCompat,
    paddings: Rect,
    margins: Rect,
): WindowInsetsCompat {
    val initialMarginBottom = margins.bottom
    val initialPaddingBottom = paddings.bottom
    val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
    val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
    if (imeVisible) {
        val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        updatePadding(bottom = initialPaddingBottom - navigationBarInsets.bottom)
    }
    updateLayoutParams<MarginLayoutParams> { updateMargins(bottom = initialMarginBottom + imeHeight) }
    return insets
}

class InsetsBuilder(
    private val view: View,
    private val paddings: Rect,
    private val margins: Rect,
) {
    private val actions =
        mutableListOf<View.(WindowInsetsCompat, Rect, Rect) -> WindowInsetsCompat>()

    fun addSystemTopPadding() {
        actions.add(View::addSystemTopPadding)
    }

    fun addSystemBottomPadding() {
        actions.add(View::addSystemBottomPadding)
    }

    fun addSystemTopMargin() {
        actions.add(View::addSystemTopMargin)
    }

    fun addSystemBottomMargin() {
        actions.add(View::addSystemBottomMargin)
    }

    fun fitIme() {
        actions.add(View::fitIme)
    }

    fun build(initialInsets: WindowInsetsCompat): WindowInsetsCompat {
        var res = initialInsets
        actions.forEach { action ->
            res = view.action(res, paddings, margins)
        }
        return res
    }
}

fun View.doOnApplyWindowInsets(block: InsetsBuilder.(WindowInsetsCompat) -> Unit) {
    val paddings = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    val margins = Rect(marginLeft, marginTop, marginRight, marginBottom)
    val builder = InsetsBuilder(this, paddings, margins)
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        builder.block(insets)
        builder.build(insets)
    }
    //requestApplyInsetsWhenAttached()
    ViewCompat.requestApplyInsets(this)
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}


