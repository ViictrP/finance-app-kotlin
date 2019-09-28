package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.domain.CartaoDomain
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.ui.custom.RialTextView

class GerenciarCartaoFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    // Repositories
    private lateinit var cartaoDomain: CartaoDomain

    // TextViews
    private lateinit var txValorLimite: RialTextView
    private lateinit var txCartaoBandeira: TextView
    private lateinit var txFechamento: TextView
    private lateinit var txNumeroCartao: TextView

    //EditTexts
    private lateinit var etDescricaoNovoCartao: EditText
    private lateinit var etDiaFechamento: EditText
    private lateinit var etBandeiraNovoCartao: EditText
    private lateinit var etLimite: CurrencyEditText
    private lateinit var etUltimosDigitos: EditText

    private lateinit var viewModel: GerenciarCartaoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (this.activity!! as MainActivity).disableActionBarButton()
        return inflater.inflate(R.layout.fragment_gerenciar_cartao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GerenciarCartaoViewModel::class.java)
        this.navController = view.findNavController()
        this.cartaoDomain = CartaoDomain(this.context!!)
        initComponents(view)
        initObservers()
    }

    private fun initObservers() {
        this.viewModel.txFechamento.observe(this, Observer {
            txFechamento.text = it
        })

        this.viewModel.txNumeroCartao.observe(this, Observer {
            txNumeroCartao.text = it
        })

        this.viewModel.txCartaoBandeira.observe(this, Observer {
            txCartaoBandeira.text = it
        })

        this.viewModel.txValorFatura.observe(this, Observer {
            txValorLimite.text = it
        })
    }

    private fun initComponents(view: View) {
        view.findViewById<CardView>(R.id.btn_salvar_novo_cartao).setOnClickListener(this)
        txValorLimite = view.findViewById(R.id.tx_valor_limite)
        txCartaoBandeira = view.findViewById(R.id.tx_cartao_bandeira)
        txFechamento = view.findViewById(R.id.tx_fechamento)
        txNumeroCartao = view.findViewById(R.id.tx_numero_cartao)

        etDescricaoNovoCartao = view.findViewById(R.id.et_descricao_novo_cartao)
        etDiaFechamento = view.findViewById(R.id.et_dia_fechamento)
        etBandeiraNovoCartao = view.findViewById(R.id.et_bandeira_novo_cartao)
        etLimite = view.findViewById(R.id.et_limite)
        etUltimosDigitos = view.findViewById(R.id.et_ultimos_digitos)
        initTextChangeListeners()
    }

    private fun initTextChangeListeners() {
        etDiaFechamento.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty())
                    viewModel.txFechamento.postValue(s.toString())
            }
        })
        etBandeiraNovoCartao.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty())
                    viewModel.txCartaoBandeira.postValue(s.toString())
            }
        })
        etLimite.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty())
                    viewModel.txValorFatura.postValue(s.toString())
            }
        })
        etUltimosDigitos.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty())
                    viewModel.txNumeroCartao.postValue(s.toString())
            }
        })
    }

    override fun onClick(button: View?) {
        val cartao = getCartao()
        this.cartaoDomain.salvarCartao(cartao) {
            Snackbar.make(
                this.view!!,
                "Cartão ${cartao.descricao} criado com sucesso.",
                Snackbar.LENGTH_SHORT
            ).show()
            navController.navigateUp()
        }
    }

    private fun getCartao(): Cartao {
        return Cartao().apply {
            this.descricao = etDescricaoNovoCartao.text.toString()
            this.dataFechamento = etDiaFechamento.text.toString().toLong()
            this.limite = etLimite.currencyDouble
            this.bandeira = etBandeiraNovoCartao.text.toString()
            this.numeroCartao = etUltimosDigitos.text.toString()
            this.usuarioId = 1L
        }
    }
}
