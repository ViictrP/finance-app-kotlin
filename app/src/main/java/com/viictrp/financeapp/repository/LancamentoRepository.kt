package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.deleteFromRealm
import java.util.*

class LancamentoRepository(private val context: Context) {

    fun findLancamentosByCarteiraId(carteiraId: Long, mes: Int, ano: Int): List<Lancamento> {
        val realm = RealmInitializer.getInstance(this.context)
        val primeiroDia = Calendar.getInstance()
        primeiroDia.set(Calendar.DAY_OF_MONTH, Constantes.UM)
        primeiroDia.set(Calendar.MONTH, mes - Constantes.UM)
        primeiroDia.set(Calendar.YEAR, ano)
        val ultimoDia = Calendar.getInstance()
        ultimoDia.set(Calendar.MONTH, primeiroDia.get(Calendar.MONTH))
        ultimoDia.set(Calendar.YEAR, primeiroDia.get(Calendar.YEAR))
        ultimoDia.set(Calendar.DAY_OF_MONTH, CustomCalendarView.ultimoDiaDoMes(mes))
        return realm.copyFromRealm(
            realm.where(Lancamento::class.java)
                .equalTo(Constantes.CARTEIRA_ID, carteiraId)
                .between(Constantes.DATA, primeiroDia.time, ultimoDia.time)
                .findAll()
                .toList()
        )
    }

    fun findLancamentosByFaturaId(faturaId: Long): List<Lancamento> {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.copyFromRealm(
            realm.where(Lancamento::class.java)
                .equalTo(Constantes.FATURA_ID, faturaId)
                .findAll()
                .toList()
        )
    }

    fun save(lancamento: Lancamento): Lancamento {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransaction {
            val lastId = it.where(Lancamento::class.java).max(Constantes.ID)
            if (lastId != null) lancamento.id = lastId.toLong() + 1 else lancamento.id = 1
            it.insert(lancamento)
        }
        return lancamento
    }

    fun deleteById(lancamentoId: Long) {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransaction {
            it.where(Lancamento::class.java)
                .equalTo(Constantes.ID, lancamentoId)
                .findFirst()
                ?.deleteFromRealm()
        }
    }

    fun findByParcelaId(parcelaId: String): List<Lancamento> {
        val realm = RealmInitializer.getInstance(this.context)
        return realm.copyFromRealm(
            realm.where(Lancamento::class.java)
                .equalTo(Constantes.PARCELA_ID, parcelaId)
                .findAll()
        )
    }
}