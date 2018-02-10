package com.github.st1hy.countthemcalories.activities.tags.fragment.model

import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder.TagItemHolder

class ClickEvent(val type: Type, val position: Int, val holder: TagItemHolder)

enum class Type { EDIT, REMOVE, OPEN }