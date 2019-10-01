package com.viictrp.financeapp.ui.cartao

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.viewObject.FaturaVO

class GerenciarFaturaViewModel : ViewModel() {
    val fatura: MutableLiveData<FaturaVO> = MutableLiveData()
}
