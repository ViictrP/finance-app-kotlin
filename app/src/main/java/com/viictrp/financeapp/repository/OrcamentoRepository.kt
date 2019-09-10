package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class OrcamentoRepository(private val context: Context) {

    fun findOrcamentoByCarteiraId(carteiraId: Long): Orcamento? {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.where<Orcamento>().equalTo(Constantes.carteiraId, carteiraId)
            .findFirst()
    }

    fun save(orcamento: Orcamento, finish: () -> Orcamento?) {
        // TODO implementar
//        val realm = RealmInitializer.getInstance(this.context)
//        realm.executeTransactionAsync {
//            val lastId = it.where<Orcamento>().max(Constantes.id)
//            if (lastId != null) orcamento.id = lastId.toLong() + 1 else orcamento.id = 1
//            it.insert(orcamento)
//            this.context.runOnUiThread(Runnable {
//                carteiraViewModel.orcamento.postValue(orcamento)
//            })
//        }
    }
}