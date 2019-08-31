package com.viictrp.financeapp.ui.lancamento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.utils.Constantes
import io.realm.kotlin.where

class LancamentoFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: LancamentoViewModel

    // NavController
    private var navController: NavController? = null

    // EditTexts
    private var etDescricao: EditText? = null
    private var etValor: CurrencyEditText? = null
    private var etData: EditText? = null
    private var etQtParcelas: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lancamento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.navController = view.findNavController()
        viewModel = ViewModelProviders.of(this).get(LancamentoViewModel::class.java)
        bindEditTexts(view)
        bindObservers()
        view.findViewById<CardView>(R.id.btn_salvar).setOnClickListener(this)
        viewModel.carteiraId.postValue(arguments?.getLong(Constantes.carteiraIdKey))
    }

    override fun onClick(view: View?) {
        val realm = RealmInitializer.getInstance(this.context!!)
        realm.executeTransaction {
            val lastId = it.where<Lancamento>().max(Constantes.id)
            val lancamento = getLancamento()
            if (lastId != null) lancamento.id = lastId.toLong() + 1 else lancamento.id = 1
            it.insert(lancamento)
        }
        Snackbar.make(this.view!!, "Lançamento criado com sucesso.", Snackbar.LENGTH_SHORT)
            .show()
        navController?.navigateUp()
    }

    private fun getLancamento(): Lancamento {
        val lancamento = Lancamento()
        lancamento.descricao = this.etDescricao!!.text.toString()
        lancamento.valor = this.etValor!!.currencyDouble
        lancamento.data = this.etData!!.text.toString()
        lancamento.quantidadeParcelas = this.etQtParcelas!!.text.toString().toInt()
        lancamento.carteiraId = viewModel.carteiraId.value
        return lancamento
    }

    /**
     * Preenche os EditTexts da tela
     */
    private fun bindEditTexts(view: View) {
        this.etDescricao = view.findViewById(R.id.et_lancamento_descricao)
        this.etValor = view.findViewById(R.id.et_lancamento_valor)
        this.etData = view.findViewById(R.id.et_lancamento_data)
        this.etQtParcelas = view.findViewById(R.id.et_lancamento_qt_parcelas)
    }

    /**
     * Inicializa os observers do viewmodel
     */
    private fun bindObservers() {
        viewModel.descricao.observe(this, Observer {
            this.etDescricao?.setText(it)
        })
        viewModel.valor.observe(this, Observer {
            this.etValor?.setText("$it")
        })
        viewModel.data.observe(this, Observer {
            this.etDescricao?.setText("$it")
        })
        viewModel.quantidadeParcelas.observe(this, Observer {
            this.etQtParcelas?.setText("$it")
        })
    }

}
