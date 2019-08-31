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

    val lancamentos: MutableLiveData<List<Lancamento>> = MutableLiveData()
    val carteira: MutableLiveData<Carteira> = _carteira
    val orcamento: MutableLiveData<Orcamento> = _orcamento
}