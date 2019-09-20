package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Carteira(mes: String? = null): RealmModel {

    @PrimaryKey
    var id: Long? = null
    var mes: String? = mes
    var ano: Int? = null
    var usuarioId: Long? = null
}