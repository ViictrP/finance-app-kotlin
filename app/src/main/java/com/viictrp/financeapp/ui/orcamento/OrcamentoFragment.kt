package com.viictrp.financeapp.ui.orcamento

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.TransitionInflater
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.ui.transitions.TextSizeTransition
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.utils.StatusBarTheme
import io.realm.kotlin.where

class OrcamentoFragment : Fragment(), OnClickListener {

    private lateinit var viewModel: OrcamentoViewModel
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initializeSharedTransitions()
        viewModel = ViewModelProviders.of(this).get(OrcamentoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_orcamento, container, false)
        root.findViewById<CardView>(R.id.cv_manage_orcamento).setOnClickListener(this)
        StatusBarTheme.setLightStatusBar(root, this.activity!!)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        buildObservers(view)
        val orcamentoId = arguments?.getLong(Constantes.orcamentoIdKey)
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        val orcamento = realm.where<Orcamento>().equalTo(Constantes.id, orcamentoId).findFirst()
        viewModel.orcamento.postValue(orcamento)
    }

    override fun onClick(cardView: View?) {
        navController!!.navigate(
            R.id.action_navegacao_orcamento_to_gerenciarOrcamento,
            bundleOf(Constantes.orcamentoIdKey to viewModel.orcamento.value?.id),
            null,
            FragmentNavigatorExtras(
                this.view!!.findViewById<TextView>(R.id.cv_manage_orcamento) to "btn_salvar"
            )
        )
    }

    private fun initializeSharedTransitions() {
        this.initializeEnterTransitions()
        this.initializeReturnTransitions()
    }

    private fun initializeEnterTransitions() {
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    private fun initializeReturnTransitions() {
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    /**
     * Inicializando os observers
     */
    private fun buildObservers(view: View) {
        val txtValorOrcamento: TextView = view.findViewById(R.id.tx_vl_orcamento)
        viewModel.orcamento.observe(this, Observer {
            txtValorOrcamento.text = "${it.valor}"
        })
    }

}
