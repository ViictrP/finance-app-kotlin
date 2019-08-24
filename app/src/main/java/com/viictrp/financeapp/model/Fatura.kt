package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Fatura(titulo: String? = null,
             descricao: String? = null,
             pago: Boolean? = null,
             mes: String? = null,
             diaFechamento: Long? = null): RealmModel {

    @PrimaryKey
    var id: Long? = null
    var titulo: String? = titulo
    var descricao: String? = descricao
    var usuarioId: Long? = null
    var pago: Boolean? = pago
    var cartaoId: Long? = null
    var mes: String? = mes
    var diaFechamento: Long? = diaFechamento
}