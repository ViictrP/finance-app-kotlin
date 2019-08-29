package com.viictrp.financeapp.ui.carteira

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.utils.StatusBarTheme
import io.realm.kotlin.where

class CarteiraFragment : Fragment(), OnClickListener {

    private var navController: NavController? = null
    private lateinit var carteiraViewModel: CarteiraViewModel
    private var txValorOrcamento: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_carteira, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carteiraViewModel = ViewModelProviders.of(this).get(CarteiraViewModel::class.java)
        StatusBarTheme.setLightStatusBar(view, this.activity!!)
        navController = view.findNavController()
        txValorOrcamento = view.findViewById(R.id.tx_vl_orcamento)
        view.findViewById<Button>(R.id.btn_orcamento).setOnClickListener(this)
        buildModelObservers(view)
        init()
    }

    override fun onClick(view: View?) {
        when(view!!.id) {
            R.id.btn_orcamento -> navController!!.navigate(
                R.id.action_navegacao_carteira_to_navegacao_orcamento,
                bundleOf(Constantes.orcamentoIdKey to carteiraViewModel.orcamento.value?.id),
                null,
                FragmentNavigatorExtras(
                    txValorOrcamento!! to "orcamento_value"
                )
            )
        }
    }

    /**
     * Inicializa os observers
     */
    private fun buildModelObservers(root: View) {
        val rvLancamentos = buildRecyclerView(root)

        carteiraViewModel.orcamento.observe(this, Observer {
            txValorOrcamento!!.text = "R$${it.valor}"
        })

        carteiraViewModel.lancamentos.observe(this, Observer {
            val adapter = rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })

        carteiraViewModel.carteira.observe(this, Observer(System.out::println))
    }

    /**
     * Inicializa os campos da tela
     */
    private fun init() {
        loadCarteira()
    }

    /**
     * Busca a carteira do mês
     */
    private fun loadCarteira() {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        val carteira = realm.where<Carteira>().equalTo(Constantes.mes, "AGOSTO").findFirst()
        if (carteira != null) {
            loadOrcamento(carteira)
            carteiraViewModel.carteira.postValue(carteira)
        } else {
            criarNovaCarteira()
        }
    }

    /**
     * Carrega o orçamento da carteira
     *
     * @param carteira carteira
     */
    private fun loadOrcamento(carteira: Carteira) {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        val orcamento = realm.where<Orcamento>().equalTo(Constantes.carteiraId, carteira.id).findFirst()
        if (orcamento == null) {
            criarNovoOrcamento(carteira.id, carteira.mes)
        } else {
            carteiraViewModel.orcamento.postValue(orcamento)
        }
    }

    /**
     * Cria um novo orçamento caso não exista na carteira
     */
    private fun criarNovoOrcamento(carteiraId: Long?, mes: String?) {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        realm.executeTransactionAsync {
            val lastId = it.where<Orcamento>().max(Constantes.id)
            val orcamento = Orcamento(0.0, mes)
            orcamento.carteiraId = carteiraId
            if (lastId != null) orcamento.id = lastId.toLong() + 1 else orcamento.id = 1
            it.insert(orcamento)
            this.activity!!.runOnUiThread(Runnable {
                carteiraViewModel.orcamento.postValue(orcamento)
            })
        }
    }

    /**
     * Cria uma nova carteira caso não exista carteiras para o mês atual
     */
    private fun criarNovaCarteira() {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        realm.executeTransactionAsync {
            val newCarteira = Carteira("AGOSTO")
            val lastId = it.where<Carteira>().max(Constantes.id)
            if (lastId != null) newCarteira.id = lastId.toLong() + 1 else newCarteira.id = 1
            newCarteira.usuarioId = 1
            it.insert(newCarteira)
            criarNovoOrcamento(newCarteira.id, newCarteira.mes)
            this.activity!!.runOnUiThread(Runnable {
                carteiraViewModel.carteira.postValue(newCarteira)
            })
        }
    }

    /**
     * Construindo a RecyclerView para listar os lançamentos
     */
    private fun buildRecyclerView(root: View): RecyclerView {
        val context = this.activity!!.applicationContext
        val rvLancamentos: RecyclerView = root.findViewById(R.id.rv_lancamentos)
        rvLancamentos.adapter = LancamentoAdapter(listOf(), context)
        rvLancamentos.layoutManager = LinearLayoutManager(context)
        return rvLancamentos
    }

}
