package com.github.st1hy.countthemcalories.utils

import android.database.Cursor
import com.github.st1hy.countthemcalories.database.IngredientTagJoint
import com.github.st1hy.countthemcalories.ui.contract.*
import dagger.Reusable
import rx.Observable
import javax.inject.Inject

@Reusable
class TagFactoryAdapter @Inject constructor() : TagFactory {

    override fun newTag(): Tag = TagAdapter()

    override fun newTag(name: String): Tag = TagAdapter(name)
}

typealias DbTag = com.github.st1hy.countthemcalories.database.Tag

class TagAdapter(val tag: DbTag) : Tag {

    constructor() : this(DbTag())
    constructor(name: String) : this(DbTag(null, name))

    override var id: Long?
        get() = tag.id
        set(value) {
            tag.id = value
        }

    override val displayName: String
        get() = tag.displayName

    override var name: String
        get() = tag.name
        set(value) {
            tag.name = value
        }

    override var creationSource: CreationSource
        get() = tag.creationSource.toUi()
        set(value) {
            tag.creationSource = value.toDb()
        }

    override val ingredientTypes: List<TaggedIngredient> by lazy {
        tag.ingredientTypes.map { ingredient -> IngredientTypeAdapter(ingredient) }
    }
}

typealias DbCreationSource = com.github.st1hy.countthemcalories.database.property.CreationSource

fun CreationSource.toDb(): DbCreationSource = when (this) {
    CreationSource.USER -> DbCreationSource.USER
    CreationSource.GENERATED -> DbCreationSource.GENERATED
}

fun DbCreationSource.toUi(): CreationSource = when (this) {
    DbCreationSource.USER -> CreationSource.USER
    DbCreationSource.GENERATED -> CreationSource.GENERATED
}

class IngredientTypeAdapter(var value: IngredientTagJoint) : TaggedIngredient

typealias DbCommandResponse<Re, Un> =
        com.github.st1hy.countthemcalories.database.commands.CommandResponse<Re, Un>

class CommandResponseAdapter<Re, Un, Re2, Un2>(
        private val parent: DbCommandResponse<Re2, Un2>,
        private val responseConvert: (res: Re2) -> Re,
        private val undoConvert: (undo: Un2) -> Un
) : CommandResponse<Re, Un> {

    override val response: Re by lazy { responseConvert(parent.response) }

    override fun isUndoAvailable(): Boolean = parent.isUndoAvailable

    override fun undoAvailability(): Observable<Boolean> = parent.undoAvailability()

    override fun undo(): Observable<CommandResponse<Un, Re>> = parent.undo()
            .map { commandResponse ->
                CommandResponseAdapter(commandResponse, undoConvert, responseConvert)
            }

    override fun invalidate() = parent.invalidate()
}

typealias DbInsertResult = com.github.st1hy.countthemcalories.database.commands.InsertResult

class InsertResponseAdapter(val parent: DbInsertResult) : InsertResult {
    override val cursor: Cursor
        get() = parent.cursor
    override val newItemPositionInCursor: Int
        get() = parent.newItemPositionInCursor
}