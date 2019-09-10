package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class CarteiraRepository(private val context: Context) {

    fun findCarteiraByMes(mes: String): Carteira? {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.where<Carteira>().equalTo(Constantes.mes, mes)
            .findFirst()
    }
}