package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.repository.CarteiraRepository

class CarteiraDomain(context: Context) {

    private val repository = CarteiraRepository(context)

    fun buscarPorMesEAno(mes: Int, ano: Int): Carteira {
        val carteira = this.repository.findCarteiraByMes(mes)
        if (carteira != null) {
            loadOrcamento(carteira)
            carteiraViewModel.carteira.postValue(carteira)
        } else {

        }
        return null
    }
}