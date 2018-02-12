package com.github.st1hy.countthemcalories.ui.contract

import android.net.Uri
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.AmountUnitType
import org.joda.time.DateTime

interface Tag {
    var id: Long?
    val displayName: String
    var name: String
    var creationSource: CreationSource
    val ingredientTypes: List<TaggedIngredient>
    val ingredientCount: Int
}

enum class CreationSource { USER, GENERATED }

interface TagFactory {
    fun newTag() : Tag
    fun newTag(name: String) : Tag
}

interface TaggedIngredient

interface IngredientTemplate {
    var id: Long?
    val amountType: AmountUnitType
    val energyDensityAmount : Double
    val displayName: String
}

interface IngredientTemplateFactory {
    fun newIngredientTemplate() : IngredientTemplate
}

interface DaoFactories : TagFactory, IngredientTemplateFactory

interface Meal {
    var id: Long?
    var name: String
    var creationDate: DateTime
    var ingredients: List<Ingredient>
    var imageUri: Uri
}

interface Ingredient {
    var amount : Double
    fun getIngredientTypeOrNull() : IngredientTemplate?
}