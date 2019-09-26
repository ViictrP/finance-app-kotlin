package com.viictrp.financeapp.ui.carteira

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.model.Orcamento
import java.util.*

class CarteiraViewModel : ViewModel() {

    val lancamentos: MutableLiveData<List<Lancamento>> = MutableLiveData()
    val carteira: MutableLiveData<Carteira> = MutableLiveData()
    val orcamento: MutableLiveData<Orcamento> = MutableLiveData()
    val progressBarProgress: MutableLiveData<Int> = MutableLiveData()
    val mesSelecionado: MutableLiveData<Int> = MutableLiveData()
    val anoSelecionado: MutableLiveData<Int> = MutableLiveData()
}