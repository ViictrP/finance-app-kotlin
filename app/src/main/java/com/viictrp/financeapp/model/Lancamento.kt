package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Lancamento(titulo: String? = null,
                 descricao: String? = null,
                 data: String? = null,
                 valor: Double? = null): RealmModel {

    @PrimaryKey
    var id: Long? = null
    var titulo: String? = titulo
    var descricao: String? = descricao
    var data: String? = data
    var valor: Double? = valor
    var quantidadeParcelas: Int = 1
    var categoriaId: Long? = null
    var carteiraId: Long? = null
    var faturaId: Long? = null

}
