package com.padi.todo_list.expandable_recyclerview

import android.content.Context
import android.util.AttributeSet

@Deprecated("Name too long",replaceWith = ReplaceWith("StickyHeader"))
class StickyHeaderRecyclerViewContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : StickyHeader(context, attrs, defStyleAttr)