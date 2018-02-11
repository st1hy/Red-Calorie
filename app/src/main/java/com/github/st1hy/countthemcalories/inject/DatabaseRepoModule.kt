package com.github.st1hy.countthemcalories.inject

import com.github.st1hy.countthemcalories.database.inject.DatabaseModule
import com.github.st1hy.countthemcalories.ui.contract.TagFactory
import com.github.st1hy.countthemcalories.ui.contract.TagsRepo
import com.github.st1hy.countthemcalories.utils.TagFactoryAdapter
import com.github.st1hy.countthemcalories.utils.TagsRepoAdapter
import dagger.Binds
import dagger.Module

@Module(includes = arrayOf(DatabaseModule::class))
abstract class DatabaseRepoModule {

    @Binds abstract fun tagFactory(factory: TagFactoryAdapter) : TagFactory

    @Binds abstract fun tagsRepo(repo: TagsRepoAdapter) : TagsRepo

    @Module companion object {

//        @JvmStatic
//        fun provideTagFactory() : TagFactory {
//
//        }

    }
}