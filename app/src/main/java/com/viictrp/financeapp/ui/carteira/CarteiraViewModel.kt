package com.viictrp.financeapp.ui.carteira

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.viewModel.CarteiraVO

class CarteiraViewModel : ViewModel() {

    val lancamentos: MutableLiveData<List<Lancamento>> = MutableLiveData()
    val carteira: MutableLiveData<CarteiraVO> = MutableLiveData()
    val progressBarProgress: MutableLiveData<Int> = MutableLiveData()
}