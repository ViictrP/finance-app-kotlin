package com.viictrp.financeapp.model

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class PagamentoFatura : RealmModel {

    @PrimaryKey
    var id: Long? = null
    var faturaId: Long? = null
    var data: Date? = null
    var valor: Double? = null
}