package com.github.st1hy.countthemcalories.ui.contract

import android.database.Cursor
import com.github.st1hy.countthemcalories.contract.model.CalorieStatistics
import org.joda.time.DateTime
import rx.Observable

interface TagsRepo {
    fun query(query: String): Observable<Cursor>
    fun query(query: String, excluded: List<String>): Observable<Cursor>
    fun query(id: Long) : Observable<Tag>
    fun insert(tag: Tag) : Observable<CommandResponse<InsertResult, Cursor>>
    fun delete(tag: Tag): Observable<CommandResponse<Cursor, InsertResult>>
    fun update(tag: Tag): Observable<Cursor>
    fun findInCursor(cursor: Cursor, tag: Tag) : Int
    fun readEntry(cursor: Cursor, tag: Tag)
    fun readName(cursor: Cursor): String
}

interface MealsRepo {
    fun getAllFilteredSortedDate(from: DateTime, to: DateTime) : Observable<List<Meal>>
    fun delete(meal: Meal) : Observable<CommandResponse<Void, Meal>>
    fun insertOrUpdate(meal: Meal, ingredients: List<Ingredient>) : Observable<Meal>
}

interface MealStatisticRepo {
    fun refresh()
    fun refresh(start: DateTime, end: DateTime)
    fun updates(): Observable<CalorieStatistics>
}

interface WeightRepo {
    fun findOneByDate(data: DateTime) : Observable<Weight>
    fun insertOrUpdate(weight: Weight) : Observable<Long>
}

typealias TagId = Long

interface IngredientsRepo {
    fun query(query : String, tags: List<String>) : Observable<Cursor>
    fun query(id: Long) : Observable<IngredientTemplate>
    fun queryRecursive(id: Long) : Observable<IngredientTemplate>
    fun findInCursor(cursor: Cursor, id: Long): Int
    fun insertOrUpdate(ingredient: IngredientTemplate, tagIds: Collection<TagId>) : Observable<IngredientTemplate>
    fun delete(ingredient: IngredientTemplate): Observable<CommandResponse<Cursor, InsertResult>>
    fun readEntry(cursor: Cursor, tag: IngredientTemplate)
    fun readName(cursor: Cursor): String
}

interface CommandResponse<Response, UndoResponse> {

    /**
     * @return response from the execution of the command
     */
    val response: Response

    /**
     * @return true if undo action is possible
     */
    fun isUndoAvailable(): Boolean

    /**
     * Provides live updates when undo action availability changes from true to false.
     * It will replay last state once subscribed, may eventually call onNext(false) and than onCompleted()
     */
    fun undoAvailability(): Observable<Boolean>

    /**
     * Traces back this operation and reverts to previous state.
     *
     * @return command for undo operation, may immediately call onError if undo is not available
     * See [CommandResponse.isUndoAvailable] to check if this will happen.
     */
    fun undo(): Observable<CommandResponse<UndoResponse, Response>>

    fun invalidate()
}

interface InsertResult {
    val cursor: Cursor
    val newItemPositionInCursor: Int
}