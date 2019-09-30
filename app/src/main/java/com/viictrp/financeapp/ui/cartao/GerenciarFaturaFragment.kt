package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.viictrp.financeapp.R

class GerenciarFaturaFragment : Fragment() {

    private lateinit var viewModel: GerenciarFaturaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gerenciar_fatura, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GerenciarFaturaViewModel::class.java)
        initChildren(view)
        init()
    }

    private fun init() {
        initObservers()
    }

    private fun initChildren(view: View) {

    }

    private fun initObservers() {

    }
}
