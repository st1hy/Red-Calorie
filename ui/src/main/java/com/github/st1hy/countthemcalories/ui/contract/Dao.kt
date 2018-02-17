package com.github.st1hy.countthemcalories.ui.contract

import android.net.Uri
import android.os.Parcelable
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.AmountUnitType
import org.joda.time.DateTime

interface Tag : Parcelable {
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
    fun newTag(tag: Tag) : Tag
}

interface TaggedIngredient {
    var tag: Tag
}

interface IngredientTemplate {
    var id: Long?
    var amountType: AmountUnitType
    var energyDensityAmount : Double
    val displayName: String
    var childIngredients: List<Ingredient>
    var imageUri: Uri
    var name: String
    var creationSource: CreationSource
    var tags: List<TaggedIngredient>
}

interface IngredientTemplateFactory {
    fun newIngredientTemplate() : IngredientTemplate
}

interface DaoFactories : TagFactory, IngredientTemplateFactory, WeightFactory

interface Meal {
    var id: Long?
    var name: String
    var creationDate: DateTime?
    var ingredients: List<Ingredient>
    var imageUri: Uri
    fun hasIngredients() = ingredients.isNotEmpty()
}

interface MealFactory {
    fun newMeal() : Meal
    fun newMeal(meal: Meal): Meal
}

interface Ingredient : Parcelable {
    var amount : Double
    fun getIngredientTypeOrNull() : IngredientTemplate?
}

interface IngredientFactory {
    fun newIngredient() : Ingredient
    fun newIngredient(template: IngredientTemplate) : Ingredient
}

interface Weight {
    var weight: Float
    var measurementDate: DateTime
}

interface WeightFactory {
    fun newWeight() : Weight
}
