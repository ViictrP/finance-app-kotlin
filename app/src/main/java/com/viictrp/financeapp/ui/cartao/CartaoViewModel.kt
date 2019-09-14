package com.viictrp.financeapp.ui.cartao

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.model.Lancamento

class CartaoViewModel : ViewModel() {

    val cartoes: MutableLiveData<List<Cartao>> = MutableLiveData()
    val cartaoSelecionado: MutableLiveData<Cartao> = MutableLiveData()
    val faturas: MutableLiveData<List<Fatura>> = MutableLiveData()
    val lancamentos: MutableLiveData<List<Lancamento>> = MutableLiveData()
}