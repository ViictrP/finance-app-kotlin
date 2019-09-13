package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class FaturaRepository(private var context: Context) {

    fun findByCartaoIdAndMes(cartaoId: Long, mes: String): List<Fatura> {
        val realm = RealmInitializer.getInstance(context)
        return realm.where<Fatura>()
            .equalTo(Constantes.CARTAO_ID, cartaoId).and()
            .equalTo(Constantes.MES, mes)
            .findAll()
            .toList()
    }

    fun save(fatura: Fatura, finish: (fatura: Fatura) -> Unit) {
        val realm = RealmInitializer.getInstance(context)
        realm.executeTransactionAsync {
            val lastId = it.where<Fatura>().max(Constantes.ID)
            if (lastId != null) fatura.id = lastId.toLong() + 1 else fatura.id = 1
            it.insert(fatura)
            finish(fatura)
        }
    }
}