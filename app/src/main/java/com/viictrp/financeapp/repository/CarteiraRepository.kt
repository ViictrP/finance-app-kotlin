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

    fun save(carteira: Carteira, finish: (carteira: Carteira?) -> Unit) {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransactionAsync {
            val lastId = it.where<Carteira>().max(Constantes.id)
            if (lastId != null) carteira.id = lastId.toLong() + 1 else carteira.id = 1
            carteira.usuarioId = 1
            it.insert(carteira)
            finish(carteira)
        }
    }
}