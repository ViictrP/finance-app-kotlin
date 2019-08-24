package com.viictrp.financeapp.ui.carteira

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.model.Orcamento
import java.util.*

class CarteiraViewModel : ViewModel() {

    private val _carteira = MutableLiveData<Carteira>().apply {
        value = Carteira(Calendar.AUGUST.toString())
    }

    private val _orcamento = MutableLiveData<Orcamento>().apply {
        value = Orcamento(0.0, Calendar.AUGUST.toString())
    }

    private val _lancamentos = MutableLiveData<List<Lancamento>>().apply {
        value = listOf(
            Lancamento("McDonalds", "Restaurante", "20/08 às 18h34", 680.99),
            Lancamento("Apple", "Tecnologia", "20/08 às 18h34", 785.89),
            Lancamento("Facebook", "Internet", "20/08 às 18h34", 1200.22),
            Lancamento("Twitter", "Internet", "20/08 às 18h34", 1223.33)
        )
    }

    val lancamentos: MutableLiveData<List<Lancamento>> = _lancamentos
    val carteira: MutableLiveData<Carteira> = _carteira
    val orcamento: MutableLiveData<Orcamento> = _orcamento
}