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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class CarteiraFragment : Fragment() {

    private lateinit var carteiraViewModel: CarteiraViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        carteiraViewModel = ViewModelProviders.of(this).get(CarteiraViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_carteira, container, false)
        val textView: TextView = root.findViewById(R.id.tx_vl_orcamento)
        val rvLancamentos = buildRecyclerView(root)
        buildModelObservers(textView, rvLancamentos)
        init()
        return root
    }

    private fun buildModelObservers(textView: TextView, rvLancamentos: RecyclerView) {
        carteiraViewModel.text.observe(this, Observer {
            textView.text = it
        })
        carteiraViewModel.lancamentos.observe(this, Observer {
            val adapter = rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
        carteiraViewModel.carteira.observe(this, Observer(System.out::println))
    }

    private fun init() {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        var carteira = realm.where<Carteira>().equalTo("mes", "AGOSTO").findFirst()
        if (carteira == null) {
            carteira = Carteira("AGOSTO")
            val lastId = realm.where<Carteira>().max("id")
            if (lastId != null) carteira.id = lastId.toLong() + 1 else carteira.id = 1
            carteira.usuarioId = 1
            realm.executeTransaction {
                it.insert(carteira)
            }
            val orcamento = Orcamento()
            orcamento.mes = "AGOSTO"
        }
        carteiraViewModel.carteira.value = carteira
    }

    private fun buildRecyclerView(root: View): RecyclerView {
        val context = this.activity!!.applicationContext
        val rvLancamentos: RecyclerView = root.findViewById(R.id.rv_lancamentos)
        rvLancamentos.adapter = LancamentoAdapter(listOf(), context)
        rvLancamentos.layoutManager = LinearLayoutManager(context)
        return rvLancamentos
    }

}