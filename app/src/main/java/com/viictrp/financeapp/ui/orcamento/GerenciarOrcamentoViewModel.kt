package com.viictrp.financeapp.ui.orcamento

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GerenciarOrcamentoViewModel : ViewModel() {

    var orcamentoId: MutableLiveData<Long> = MutableLiveData()
    var valor: MutableLiveData<Double> = MutableLiveData()
}
