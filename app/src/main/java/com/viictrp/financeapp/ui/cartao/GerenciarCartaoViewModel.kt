package com.viictrp.financeapp.ui.cartao

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Cartao

class GerenciarCartaoViewModel : ViewModel() {

    val cartao: MutableLiveData<Cartao> = MutableLiveData()

    val txValorFatura: MutableLiveData<String> = MutableLiveData()
    val txCartaoBandeira: MutableLiveData<String> = MutableLiveData()
    val txFechamento: MutableLiveData<String> = MutableLiveData()
    val txNumeroCartao: MutableLiveData<String> = MutableLiveData()
}
