package com.viictrp.financeapp.ui.orcamento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.repository.OrcamentoRepository
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class GerenciarOrcamento : Fragment(), OnClickListener {

    private lateinit var viewModel: GerenciarOrcamentoViewModel
    private lateinit var orcamentoRepository: OrcamentoRepository
    private var currencyEditText: CurrencyEditText? = null
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (this.activity!! as MainActivity).disableActionBarButton()
        viewModel = ViewModelProviders.of(this).get(GerenciarOrcamentoViewModel::class.java)
        return inflater.inflate(R.layout.fragment_gerenciar_orcamento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        view.findViewById<CardView>(R.id.btn_salvar).setOnClickListener(this)
        currencyEditText = view.findViewById(R.id.orcamentoInput)
        this.orcamentoRepository = OrcamentoRepository(this.context!!)
        viewModel.valor.observe(this, Observer {
            currencyEditText!!.setText("$it")
        })
        loadOrcamento()
    }

    override fun onClick(button: View?) {
        val realm = RealmInitializer.getInstance(this.context!!)
        realm.executeTransactionAsync {
            val orcamento = it.where<Orcamento>()
                .equalTo(Constantes.ID, viewModel.orcamentoId.value)
                .findFirst()
            orcamento!!.valor = currencyEditText!!.currencyDouble
        }
        Snackbar.make(this.view!!, "Orçamento atualizado com sucesso.", Snackbar.LENGTH_SHORT)
            .show()
        navController?.navigateUp()
    }

    /**
     * Busca o orçamento no realm
     */
    private fun loadOrcamento() {
        val orcamentoId = arguments?.getLong(Constantes.ORCAMENTO_ID_KEY)
        viewModel.orcamentoId.postValue(orcamentoId)
        val orcamento = orcamentoRepository.findById(orcamentoId!!)
        viewModel.valor.postValue(orcamento?.valor)
    }

}
