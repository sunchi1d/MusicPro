
package com.dirror.music.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 一直获取焦点的文本视图
 *
 */
class MarqueeTextView: AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun isFocused(): Boolean {
        return true
    }
}