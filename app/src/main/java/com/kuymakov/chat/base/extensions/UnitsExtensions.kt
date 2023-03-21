package com.kuymakov.chat.base.extensions

import android.content.res.Resources
import kotlin.math.roundToInt

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Float.sp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity


val Int.dp: Int
    get() = this * Resources.getSystem().displayMetrics.density.roundToInt()