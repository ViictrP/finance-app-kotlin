package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.deleteFromRealm
import io.realm.kotlin.where

class LancamentoRepository(private val context: Context) {

    fun findLancamentosByCarteiraId(carteiraId: Long): List<Lancamento> {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.copyFromRealm(
            realm.where<Lancamento>()
                .equalTo(Constantes.CARTEIRA_ID, carteiraId)
                .findAll()
                .toList()
        )
    }

    fun findLancamentosByFaturaId(faturaId: Long): List<Lancamento> {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.copyFromRealm(
            realm.where<Lancamento>()
                .equalTo(Constantes.FATURA_ID, faturaId)
                .findAll()
                .toList()
        )
    }

    fun save(lancamento: Lancamento, finish: (lancamento: Lancamento?) -> Unit) {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransaction {
            val lastId = it.where<Lancamento>().max(Constantes.ID)
            if (lastId != null) lancamento.id = lastId.toLong() + 1 else lancamento.id = 1
            it.insert(lancamento)
            finish(lancamento)
        }
    }

    fun delete(lancamento: Lancamento) {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransaction {
            it.where<Lancamento>()
                .equalTo(Constantes.ID, lancamento.id)
                .findFirst()
                ?.deleteFromRealm()
        }
    }
}