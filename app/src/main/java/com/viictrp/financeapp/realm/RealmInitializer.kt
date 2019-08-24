package com.viictrp.financeapp.realm

import android.content.Context
import io.realm.Realm

class RealmInitializer {

    companion object RealmFactory {

        fun getInstance(context: Context): Realm {
            Realm.init(context)
            return Realm.getDefaultInstance()
        }
    }
}