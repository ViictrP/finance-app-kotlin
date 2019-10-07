package com.viictrp.financeapp.exceptions

import java.lang.RuntimeException

class RealmNotFoundException(message: String) : RuntimeException(message) {

    private val code
        get() = 404L
}