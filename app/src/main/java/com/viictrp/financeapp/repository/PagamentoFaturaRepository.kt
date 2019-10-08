package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.PagamentoFatura
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where
import java.util.*

class PagamentoFaturaRepository(private val context: Context) {

    fun save(pagamentoFatura: PagamentoFatura): PagamentoFatura {
        val realm = RealmInitializer.getInstance(context)
        realm.executeTransaction {
            val lastId = it.where<PagamentoFatura>().max(Constantes.ID)
            if (lastId != null) pagamentoFatura.id = lastId.toLong() + 1 else pagamentoFatura.id = 1
            it.insert(pagamentoFatura)
        }
        return pagamentoFatura
    }

    fun findByFaturaId(faturaId: Long): List<PagamentoFatura> {
        val realm = RealmInitializer.getInstance(context)
        val pagamentos = realm.where<PagamentoFatura>()
            .equalTo(Constantes.FATURA_ID, faturaId)
            .findAll()
        return realm.copyFromRealm(pagamentos)
    }

    fun findByFaturaIdAndMesAndAno(faturaId: Long, mes: String, ano: Int): List<PagamentoFatura> {
        val realm = RealmInitializer.getInstance(context)
        val pagamentos = realm.where<PagamentoFatura>()
            .equalTo(Constantes.FATURA_ID, faturaId).and()
            .equalTo(Constantes.MES, mes).and()
            .equalTo(Constantes.ANO, ano)
            .findAll()
        return realm.copyFromRealm(pagamentos)
    }

    fun findById(pagamentoId: Long): PagamentoFatura? {
        val realm = RealmInitializer.getInstance(context)
        val managedObject = realm.where<PagamentoFatura>()
            .equalTo(Constantes.ID, pagamentoId)
            .findFirst()
        return realm.copyFromRealm(managedObject!!)
    }
}