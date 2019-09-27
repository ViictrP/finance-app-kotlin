package com.viictrp.financeapp.transformation

abstract class Assembler<E, R> {

    abstract fun toEntity(r: R): E
    abstract fun toViewObject(e: E): R

    fun toEntity(resources: List<R>): List<E> {
        return resources.map {
            toEntity(it)
        }
    }

    fun toViewObject(entities: List<E>): List<R> {
        return entities.map {
            toViewObject(it)
        }
    }
}