package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.viictrp.financeapp.R
import com.viictrp.financeapp.utils.StatusBarTheme

class CartaoFragment : Fragment() {

    private lateinit var cartaoViewModel: CartaoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        cartaoViewModel = ViewModelProviders.of(this).get(CartaoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cartao, container, false)
        StatusBarTheme.setLightStatusBar(root, this.activity!!)
        val textView: TextView = root.findViewById(R.id.tx_cartao)
        cartaoViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}