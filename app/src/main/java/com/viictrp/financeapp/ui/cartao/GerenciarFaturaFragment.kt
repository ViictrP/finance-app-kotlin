package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.viictrp.financeapp.R
import com.viictrp.financeapp.domain.CartaoDomain
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.utils.Constantes

class GerenciarFaturaFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: GerenciarFaturaViewModel
    private lateinit var cartaoDomain: CartaoDomain

    // VIEW ELEMENTS
    private lateinit var cetValor: CurrencyEditText
    private lateinit var btnPagar: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gerenciar_fatura, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GerenciarFaturaViewModel::class.java)
        cartaoDomain = CartaoDomain(this.context!!)
        initChildren(view)
        init()
    }

    override fun onClick(view: View?) {

    }

    private fun init() {
        initObservers()
        val faturaId = arguments?.getLong(Constantes.FATURA_ID_KEY)
        val fatura = cartaoDomain.buscarFaturaPorId(faturaId!!)
        viewModel.fatura.postValue(fatura)
    }

    private fun initChildren(view: View) {
        this.cetValor = view.findViewById(R.id.cet_valor)
        this.btnPagar = view.findViewById(R.id.btn_pagar)
        this.btnPagar.setOnClickListener(this)
    }

    private fun initObservers() {

    }
}
