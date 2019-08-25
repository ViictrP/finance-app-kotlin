package com.viictrp.financeapp.ui.orcamento

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viictrp.financeapp.R


class Orcamento : Fragment() {

    companion object {
        fun newInstance() = Orcamento()
    }

    private lateinit var viewModel: OrcamentoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.orcamento_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrcamentoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
