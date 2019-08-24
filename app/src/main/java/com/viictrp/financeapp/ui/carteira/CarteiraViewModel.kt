package com.viictrp.financeapp.ui.carteira

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Lancamento
import java.util.*

class CarteiraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "R$12.586,99"
    }

    private val _carteira = MutableLiveData<Carteira>().apply {
        value = Carteira(Calendar.AUGUST.toString())
    }

    private val _lancamentos = MutableLiveData<List<Lancamento>>().apply {
        value = listOf(
            Lancamento("McDonalds", "Restaurante", "20/08 às 18h34", 680.99),
            Lancamento("Apple", "Tecnologia", "20/08 às 18h34", 785.89),
            Lancamento("Facebook", "Internet", "20/08 às 18h34", 1200.22),
            Lancamento("Twitter", "Internet", "20/08 às 18h34", 1223.33)
        )
    }

    val text: LiveData<String> = _text
    val lancamentos: MutableLiveData<List<Lancamento>> = _lancamentos
    val carteira: MutableLiveData<Carteira> = _carteira
}