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
import com.viictrp.financeapp.model.Lancamento

class CarteiraFragment : Fragment() {

    private lateinit var carteiraViewModel: CarteiraViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        carteiraViewModel = ViewModelProviders.of(this).get(CarteiraViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_carteira, container, false)
        val textView: TextView = root.findViewById(R.id.tx_vl_orcamento)
        val rvLancamentos = buildRecyclerView(root)
        carteiraViewModel.text.observe(this, Observer {
            textView.text = it
        })
        carteiraViewModel.lancamentos.observe(this, Observer {
            val adapter = rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
        return root
    }

    private fun buildRecyclerView(root: View): RecyclerView {
        val context = this.activity!!.applicationContext
        val rvLancamentos: RecyclerView = root.findViewById(R.id.rv_lancamentos)
        rvLancamentos.adapter = LancamentoAdapter(listOf(), context)
        val layoutManager = LinearLayoutManager(context)
        rvLancamentos.layoutManager = layoutManager
        return rvLancamentos
    }

}