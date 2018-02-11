package com.github.st1hy.countthemcalories.ui.activities.tags.model

import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagItemHolder

class ClickEvent(val type: Type, val position: Int, val holder: TagItemHolder)

enum class Type { EDIT, REMOVE, OPEN }