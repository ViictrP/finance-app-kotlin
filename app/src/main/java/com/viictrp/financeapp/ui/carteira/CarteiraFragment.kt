package com.viictrp.financeapp.ui.carteira

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import io.realm.Realm
import io.realm.kotlin.where

class CarteiraFragment : Fragment() {

    private lateinit var carteiraViewModel: CarteiraViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        carteiraViewModel = ViewModelProviders.of(this).get(CarteiraViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_carteira, container, false)
        buildModelObservers(root)
        init()
        return root
    }

    private fun buildModelObservers(root: View) {
        val txValorOrcamento: TextView = root.findViewById(R.id.tx_vl_orcamento)
        val rvLancamentos = buildRecyclerView(root)

        carteiraViewModel.orcamento.observe(this, Observer {
            txValorOrcamento.text = "R$${it.valor}"
        })

        carteiraViewModel.lancamentos.observe(this, Observer {
            val adapter = rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })

        carteiraViewModel.carteira.observe(this, Observer(System.out::println))
    }

    private fun init() {
        loadCarteira()
    }

    private fun loadCarteira() {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        val carteira = realm.where<Carteira>().equalTo("mes", "AGOSTO").findFirst()
        if (carteira != null) {
            loadOrcamento(carteira)
            carteiraViewModel.carteira.postValue(carteira)
        } else {
            criarNovaCarteira()
        }
    }

    private fun loadOrcamento(carteira: Carteira) {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        val orcamento = realm.where<Orcamento>().equalTo("carteiraId", carteira.id).findFirst()
        if (orcamento == null) {
            criarNovoOrcamento(carteira.id, carteira.mes)
        } else {
            carteiraViewModel.orcamento.postValue(orcamento)
        }
    }

    private fun criarNovoOrcamento(carteiraId: Long?, mes: String?) {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        realm.executeTransactionAsync {
            val lastId = it.where<Orcamento>().max("id")
            val orcamento = Orcamento(0.0, mes)
            orcamento.carteiraId = carteiraId
            if (lastId != null) orcamento.id = lastId.toLong() + 1 else orcamento.id = 1
            it.insert(orcamento)
            this.activity!!.runOnUiThread(Runnable {
                carteiraViewModel.orcamento.postValue(orcamento)
            })
        }
    }

    private fun criarNovaCarteira() {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        realm.executeTransactionAsync {
            val newCarteira = Carteira("AGOSTO")
            val lastId = it.where<Carteira>().max("id")
            if (lastId != null) newCarteira.id = lastId.toLong() + 1 else newCarteira.id = 1
            newCarteira.usuarioId = 1
            it.insert(newCarteira)
            criarNovoOrcamento(newCarteira.id, newCarteira.mes)
            this.activity!!.runOnUiThread(Runnable {
                carteiraViewModel.carteira.postValue(newCarteira)
            })
        }
    }

    private fun buildRecyclerView(root: View): RecyclerView {
        val context = this.activity!!.applicationContext
        val rvLancamentos: RecyclerView = root.findViewById(R.id.rv_lancamentos)
        rvLancamentos.adapter = LancamentoAdapter(listOf(), context)
        rvLancamentos.layoutManager = LinearLayoutManager(context)
        return rvLancamentos
    }

}
