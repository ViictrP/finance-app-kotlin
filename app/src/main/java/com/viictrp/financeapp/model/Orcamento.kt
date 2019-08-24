package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Orcamento(valor: Double? = null,
                mes: String? = null): RealmModel {

    @PrimaryKey
    var id: Long? = null
    var valor: Double? = valor
    var mes: String? = mes
    var carteiraId: Long? = null
}