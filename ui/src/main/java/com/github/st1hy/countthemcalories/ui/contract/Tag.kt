package com.github.st1hy.countthemcalories.ui.contract

interface Tag {
    var id: Long?
    val displayName: String
    var name: String
    var creationSource: CreationSource
    val ingredientTypes: List<TaggedIngredient>
}

enum class CreationSource { USER, GENERATED }

interface TagFactory {
    fun newTag() : Tag
    fun newTag(name: String) : Tag
}

interface TaggedIngredient