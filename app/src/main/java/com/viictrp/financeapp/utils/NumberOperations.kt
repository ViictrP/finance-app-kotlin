package com.viictrp.financeapp.utils

class NumberOperations {

    companion object {

        fun getPercentFrom(minor: Double, total: Double): Double {
            return if (minor >= total) 100.toDouble()
            else ((minor / total) * 100)
        }
    }
}