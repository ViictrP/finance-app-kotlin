package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class CartaoRepository(private var context: Context) {

    fun findCartaoByUsuarioId(usuarioId: Long): List<Cartao> {
        val realm = RealmInitializer.getInstance(context)
        return realm.where<Cartao>()
            .equalTo(Constantes.USUARIO_ID, usuarioId)
            .findAll()
            .toList()
    }

    fun save(cartao: Cartao, finish: (cartao: Cartao) -> Unit) {
        val realm = RealmInitializer.getInstance(context)
        realm.executeTransactionAsync {
            val lastId = it.where<Cartao>().max(Constantes.ID)
            if (lastId != null) cartao.id = lastId.toLong() + 1 else cartao.id = 1
            it.insert(cartao)
            finish(cartao)
        }
    }
}