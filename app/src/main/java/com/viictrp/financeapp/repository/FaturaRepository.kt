package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.exceptions.RealmNotFoundException
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class FaturaRepository(private var context: Context) {

    fun findByCartaoIdAndMesAndAno(cartaoId: Long, mes: String, ano: Int): Fatura? {
        val realm = RealmInitializer.getInstance(context)
        val managedObject = realm.where<Fatura>()
            .equalTo(Constantes.CARTAO_ID, cartaoId).and()
            .equalTo(Constantes.MES, mes).and()
            .equalTo(Constantes.ANO, ano)
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

    @Throws(RealmNotFoundException::class)
    fun update(fatura: Fatura) {
        val realm = RealmInitializer.getInstance(context)
        val managedFatura = realm.where<Fatura>()
            .equalTo(Constantes.ID, fatura.id!!)
            .findFirst()
            ?: throw RealmNotFoundException("Fatura não encontrada")
        realm.executeTransaction {
            managedFatura.ano = fatura.ano
            managedFatura.descricao = fatura.descricao
            managedFatura.diaFechamento = fatura.diaFechamento
            managedFatura.mes = fatura.mes
            managedFatura.pago = fatura.pago
            managedFatura.titulo = fatura.titulo
        }
    }
}