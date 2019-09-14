package com.viictrp.financeapp.ui.cartao

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R

class GerenciarCartao : Fragment() {

    private lateinit var viewModel: GerenciarCartaoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (this.activity!! as MainActivity).disableActionBarButton()
        return inflater.inflate(R.layout.fragment_gerenciar_cartao, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GerenciarCartaoViewModel::class.java)
    }

}
