package com.viictrp.financeapp.ui.orcamento

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.viictrp.financeapp.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class GerenciarOrcamento : Fragment() {

    private lateinit var viewModel: GerenciarOrcamentoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       viewModel = ViewModelProviders.of(this).get(GerenciarOrcamentoViewModel::class.java)
        return inflater.inflate(R.layout.gerenciar_orcamento_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input = view.findViewById<EditText>(R.id.orcamentoInput)
        viewModel.orcamento.observe(this, Observer {
            input.setText("R$${it.valor}")
        })
        loadOrcamento()
    }

    /**
     * Busca o orçamento no realm
     */
    private fun loadOrcamento() {
        val realm = RealmInitializer.getInstance(this.context!!)
        val orcamentoId = arguments?.getLong(Constantes.orcamentoIdKey)
        val orcamento = realm.where<Orcamento>().equalTo(Constantes.id, orcamentoId).findFirst()
        viewModel.orcamento.postValue(orcamento)
    }

}
