package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Cartao(descricao: String? = null,
             dataFechamento: Long? = null,
             limite: Double? = null): RealmModel {

    @PrimaryKey
    var id: Long? = null
    var descricao: String? = descricao
    var dataFechamento: Long? = dataFechamento
    var limite: Double? = limite
    var limiteDisponivel: Double? = limite
    var bandeira: String? = null
    var numeroCartao: String? = null
    var usuarioId: Long? = null
}