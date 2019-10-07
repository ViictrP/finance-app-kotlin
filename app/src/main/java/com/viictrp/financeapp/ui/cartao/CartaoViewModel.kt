package com.viictrp.financeapp.ui.cartao

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.viewObject.LancamentoVO

class CartaoViewModel : ViewModel() {

    val cartoes: MutableLiveData<List<Cartao>> = MutableLiveData()
    val cartaoSelecionado: MutableLiveData<Cartao> = MutableLiveData()
    val lancamentos: MutableLiveData<List<LancamentoVO>> = MutableLiveData()
    val mesSelecionado: MutableLiveData<Int> = MutableLiveData()
    val anoSelecionado: MutableLiveData<Int> = MutableLiveData()
}