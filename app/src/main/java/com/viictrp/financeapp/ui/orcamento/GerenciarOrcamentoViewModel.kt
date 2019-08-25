package com.viictrp.financeapp.ui.orcamento

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Orcamento

class GerenciarOrcamentoViewModel : ViewModel() {

    var orcamento: MutableLiveData<Orcamento> = MutableLiveData()
}
