package com.viictrp.financeapp.repository

import android.content.Context
import com.viictrp.financeapp.exceptions.RealmNotFoundException
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.Realm
import io.realm.kotlin.where

class CartaoRepository(private var context: Context) {

    fun findCartaoByUsuarioId(usuarioId: Long): List<Cartao> {
        val realm = RealmInitializer.getInstance(context)
        return realm.copyFromRealm(
            realm.where<Cartao>()
                .equalTo(Constantes.USUARIO_ID, usuarioId)
                .findAll()
                .toList()
        )
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

    fun findById(cartaoId: Long): Cartao? {
        val realm = RealmInitializer.getInstance(context)
        val managedObject = realm.where<Cartao>().equalTo(Constantes.ID, cartaoId)
            .findFirst()
        return if (managedObject != null) realm.copyFromRealm(managedObject)
        else null
    }

    @Throws(RealmNotFoundException::class)
    fun update(cartao: Cartao) {
        val realm = RealmInitializer.getInstance(context)
        val managedObject = realm.where(Cartao::class.java)
            .equalTo(Constantes.ID, cartao.id!!)
            .findFirst()
            ?: throw RealmNotFoundException("Cartão não encontrado")
        realm.executeTransaction {
            managedObject.bandeira = cartao.bandeira
            managedObject.dataFechamento = cartao.dataFechamento
            managedObject.descricao = cartao.descricao
            managedObject.limite = cartao.limite
            managedObject.limiteDisponivel = cartao.limiteDisponivel
            managedObject.numeroCartao = cartao.numeroCartao
        }
    }
}