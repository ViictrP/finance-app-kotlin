package com.viictrp.financeapp.ui.carteira

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.viewObject.CarteiraVO
import com.viictrp.financeapp.viewObject.LancamentoVO

class CarteiraViewModel : ViewModel() {

    val lancamentos: MutableLiveData<List<LancamentoVO>> = MutableLiveData()
    val carteira: MutableLiveData<CarteiraVO> = MutableLiveData()
    val progressBarProgress: MutableLiveData<Int> = MutableLiveData()
}