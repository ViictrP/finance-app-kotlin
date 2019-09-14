package com.viictrp.financeapp.ui.lancamento

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class LancamentoViewModel : ViewModel() {
    var carteiraId: MutableLiveData<Long> = MutableLiveData()
    var faturaId: MutableLiveData<Long> = MutableLiveData()
    var titulo: MutableLiveData<String> = MutableLiveData()
    var descricao: MutableLiveData<String> = MutableLiveData()
    var valor: MutableLiveData<Double> = MutableLiveData()
    var data: MutableLiveData<Date> = MutableLiveData()
    var quantidadeParcelas: MutableLiveData<Int> = MutableLiveData()
}
