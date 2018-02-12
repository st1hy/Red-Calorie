package com.github.st1hy.countthemcalories.utils

import android.database.Cursor
import com.github.st1hy.countthemcalories.database.commands.tags.TagsDatabaseCommands
import com.github.st1hy.countthemcalories.database.rx.RxTagsDatabaseModel
import com.github.st1hy.countthemcalories.ui.contract.CommandResponse
import com.github.st1hy.countthemcalories.ui.contract.InsertResult
import com.github.st1hy.countthemcalories.ui.contract.Tag
import com.github.st1hy.countthemcalories.ui.contract.TagsRepo
import dagger.Reusable
import rx.Observable
import javax.inject.Inject

@Reusable
class TagsRepoAdapter @Inject constructor() : TagsRepo {

    @Inject lateinit var model: RxTagsDatabaseModel
    @Inject lateinit var commands: TagsDatabaseCommands

    override fun query(query: String): Observable<Cursor> = model.getAllFiltered(query)

    override fun query(id: Long): Observable<Tag> = model.getById(id)
            .map { tag -> TagAdapter(tag) }

    override fun insert(tag: Tag): Observable<CommandResponse<InsertResult, Cursor>> =
            commands.insert(inner(tag))
                    .map { response ->
                        CommandResponseAdapter(response, dbInsertResultToUi, ::oneToOne)
                    }

    override fun delete(tag: Tag): Observable<CommandResponse<Cursor, InsertResult>> =
            commands.delete(inner(tag))
                    .map { response ->
                        CommandResponseAdapter(response, ::oneToOne, dbInsertResultToUi)
                    }

    override fun update(tag: Tag): Observable<Cursor> = model.updateRefresh(inner(tag))

    override fun findInCursor(cursor: Cursor, tag: Tag): Int = model.findInCursor(cursor,
            inner(tag))

    override fun readEntry(cursor: Cursor, tag: Tag) = model.performReadEntity(cursor, inner(tag))

    override fun readName(cursor: Cursor): String = model.readName(cursor)

    private fun inner(tag: Tag) = (tag as TagAdapter).tag

}

private val dbInsertResultToUi: (DbInsertResult) -> InsertResult = { db: DbInsertResult ->
    InsertResponseAdapter(db)
}

private fun <T> oneToOne(t: T) = t