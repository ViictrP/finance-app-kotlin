package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Categoria(titulo: String? = null,
                descricao: String? = null): RealmModel {

    @PrimaryKey
    var id: Long? = null
    var titulo: String? = titulo
    var descricao: String? = descricao
    var usuarioId: Long? = null
}