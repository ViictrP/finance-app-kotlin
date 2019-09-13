package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.CartaoAdapter
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.ui.custom.CarouselRecyclerView
import com.viictrp.financeapp.utils.StatusBarTheme

class CartaoFragment : Fragment() {

    private lateinit var cartaoViewModel: CartaoViewModel
    private lateinit var crCartoes: CarouselRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cartao, container, false)
        StatusBarTheme.setLightStatusBar(root, this.activity!!)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartaoViewModel = ViewModelProviders.of(this).get(CartaoViewModel::class.java)
        initChildren(view)
        initObservers()
        init()
    }

    private fun init() {
        loadCartoes()
    }

    private fun initObservers() {
        cartaoViewModel.cartoes.observe(this, Observer {
            val adapter = this.crCartoes.adapter as CartaoAdapter
            adapter.setList(it.toMutableList())
        })
    }

    private fun initChildren(root: View) {
        this.crCartoes = root.findViewById(R.id.cr_cartoes)
        this.crCartoes.initialize(CartaoAdapter(mutableListOf(), this.context!!))
    }

    private fun loadCartoes() {
        val cartoes = mutableListOf(Cartao().apply {
            this.limite = 12500.toDouble()
            this.dataFechamento = 10
            this.descricao = "Itaucard Visa Gold"
        }, Cartao().apply {
            this.limite = 8500.toDouble()
            this.dataFechamento = 10
            this.descricao = "Itaucard Tam"
        })
        cartaoViewModel.cartoes.postValue(cartoes)
    }
}