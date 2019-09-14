package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class FaturaRepository(private var context: Context) {

    fun findByCartaoIdAndMes(cartaoId: Long, mes: String): Fatura? {
        val realm = RealmInitializer.getInstance(context)
        val managedObject = realm.where<Fatura>()
            .equalTo(Constantes.CARTAO_ID, cartaoId).and()
            .equalTo(Constantes.MES, mes)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    fun findById(faturaId: Long): Fatura? {
        val realm = RealmInitializer.getInstance(context)
        val managedObject = realm.where<Fatura>()
            .equalTo(Constantes.ID, faturaId)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    fun save(fatura: Fatura) {
        val realm = RealmInitializer.getInstance(context)
        realm.executeTransaction {
            val lastId = it.where<Fatura>().max(Constantes.ID)
            if (lastId != null) fatura.id = lastId.toLong() + 1 else fatura.id = 1
            it.insert(fatura)
        }
    }
}