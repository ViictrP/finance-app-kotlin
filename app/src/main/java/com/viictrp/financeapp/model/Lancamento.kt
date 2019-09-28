package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.Date

@RealmClass
open class Lancamento(
    titulo: String? = null,
    descricao: String? = null,
    data: Date? = null,
    valor: Double? = null
) : RealmModel {

    @PrimaryKey
    var id: Long? = null
    var titulo: String? = titulo
    var descricao: String? = descricao
    var data: Date? = data
    var valor: Double? = valor
    var quantidadeParcelas: Int = 1
    var categoriaId: Long? = null
    var parcelaId: String? = null
    var carteiraId: Long? = null
    var faturaId: Long? = null
    var numeroParcela: Int = 1

}
