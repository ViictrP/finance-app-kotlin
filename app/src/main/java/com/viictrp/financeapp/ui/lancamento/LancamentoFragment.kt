package com.viictrp.financeapp.ui.lancamento

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viictrp.financeapp.R


class LancamentoFragment : Fragment() {

    companion object {
        fun newInstance() = LancamentoFragment()
    }

    private lateinit var viewModel: LancamentoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lancamento, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LancamentoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
