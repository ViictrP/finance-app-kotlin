package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class CarteiraRepository(private val context: Context) {

    fun findCarteiraByMes(mes: String, ano: Int): Carteira? {
        val realm = RealmInitializer.getInstance(this.context)
        val managedObject = realm.where<Carteira>()
            .equalTo(Constantes.MES, mes)
            .equalTo(Constantes.ANO, ano)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    fun findById(carteiraId: Long): Carteira? {
        val realm = RealmInitializer.getInstance(this.context)
        val managedObject = realm.where<Carteira>().equalTo(Constantes.ID, carteiraId)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    fun save(carteira: Carteira): Carteira {
        val realm = RealmInitializer.getInstance(this.context)
        realm.executeTransaction {
            val lastId = it.where<Carteira>().max(Constantes.ID)
            if (lastId != null) carteira.id = lastId.toLong() + 1 else carteira.id = 1
            carteira.usuarioId = 1
            it.insert(carteira)
        }
        return carteira
    }
}