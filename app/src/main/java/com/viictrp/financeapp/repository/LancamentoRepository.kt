package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class LancamentoRepository(private val context: Context) {

    fun findLancamentosByCarteiraId(carteiraId: Long): List<Lancamento> {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.where<Lancamento>()
            .equalTo(Constantes.carteiraId, carteiraId)
            .findAll()
            .toList()
    }
}