package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class OrcamentoRepository(private val context: Context) {

    fun findOrcamentoByCarteiraId(carteiraId: Long): Orcamento? {
        val realm = RealmInitializer.getInstance(this.context)
        val managedObject = realm.where<Orcamento>().equalTo(Constantes.CARTEIRA_ID, carteiraId)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    fun findById(orcamentoId: Long): Orcamento? {
        val realm = RealmInitializer.getInstance(this.context)
        val managedObject = realm.where<Orcamento>().equalTo(Constantes.ID, orcamentoId)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    fun save(orcamento: Orcamento): Orcamento {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransaction {
            val lastId = it.where<Orcamento>().max(Constantes.ID)
            if (lastId != null) orcamento.id = lastId.toLong() + 1 else orcamento.id = 1
            it.insert(orcamento)
        }
        return orcamento
    }
}