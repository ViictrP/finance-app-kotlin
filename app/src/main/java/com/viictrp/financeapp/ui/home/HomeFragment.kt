package com.viictrp.financeapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.utils.StatusBarTheme

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        StatusBarTheme.setLightStatusBar(root, this.activity!!)
        val textView: TextView = root.findViewById(R.id.tx_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        (this.activity!! as MainActivity).disableActionBarButton()
        return root
    }
}