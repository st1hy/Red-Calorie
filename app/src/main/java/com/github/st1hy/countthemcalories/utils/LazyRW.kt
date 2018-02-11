package com.github.st1hy.countthemcalories.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> lazyRW(initialize: () -> T) : LazyRW<T> = SynchronizedLazyRW(initialize)

interface LazyRW<T> : ReadWriteProperty<Any, T>

private object UninitializedValue

@Suppress("UNCHECKED_CAST")
private class SynchronizedLazyRW<T>(private val initialize: () -> T) : LazyRW<T> {
    @Volatile var value: Any = UninitializedValue
    private val lock = Any()

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val v1 = value
        if (v1 == UninitializedValue) {
            synchronized(lock) {
                val v2 = value
                if (v2 == UninitializedValue) {
                    value = initialize()!!
                }
            }
        }
        return value as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value!!
    }
}