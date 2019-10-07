package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.R
import com.viictrp.financeapp.domain.CartaoDomain
import com.viictrp.financeapp.domain.LancamentoDomain
import com.viictrp.financeapp.exceptions.RealmNotFoundException
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewObject.FaturaVO
import java.util.*

class GerenciarFaturaFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: GerenciarFaturaViewModel
    private lateinit var cartaoDomain: CartaoDomain
    private lateinit var lancamentoDomain: LancamentoDomain

    // VIEW ELEMENTS
    private lateinit var navController: NavController
    private lateinit var cetValor: CurrencyEditText
    private lateinit var btnPagar: CardView
    private lateinit var txMesFatura: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gerenciar_fatura, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        viewModel = ViewModelProviders.of(this).get(GerenciarFaturaViewModel::class.java)
        cartaoDomain = CartaoDomain(this.context!!)
        lancamentoDomain = LancamentoDomain(this.context!!)
        initChildren(view)
        init()
    }

    override fun onClick(view: View?) {
        val valor = cetValor.currencyDouble
        val fatura = viewModel.fatura.value!!
        try {
            cartaoDomain.pagarFatura(fatura, valor)
            verificarValorTotalPago(fatura, valor)
            mostrarMensagemESair(
                "Pagamento no valor $valor para fatura do mês de ${fatura.mes}/${fatura.ano} realizado com sucesso",
                true
            )
        } catch (exception: RealmNotFoundException) {
            mostrarMensagemESair(exception.message!!, false)
        }
    }

    private fun verificarValorTotalPago(fatura: FaturaVO, valorPago: Double) {
        val lancamentos = lancamentoDomain.buscarLancamentosDaFatura(fatura.id!!)
        val valorTotal = lancamentoDomain.calcularValorTotal(lancamentos)
        if (valorPago < valorTotal) {
            gerarNovoLancamento(fatura, valorTotal - valorPago)
        }
    }

    private fun gerarNovoLancamento(fatura: FaturaVO, saldo: Double) {
        val faturaFechada = verificarSeFaturaEstaFechada(fatura)
        val lancamento = Lancamento().apply {
            this.quantidadeParcelas = Constantes.UM
            this.valor = saldo
            this.numeroParcela = Constantes.UM
            this.faturaId = fatura.id!!
            this.descricao = "Saldo da fatura ${fatura.mes!!}/${fatura.ano!!}"
            this.data = obterDataNovoLancamento(faturaFechada)
            this.titulo = this.descricao
        }
        lancamentoDomain.salvarNoCartao(lancamento, fatura.cartaoId!!)
    }

    private fun verificarSeFaturaEstaFechada(fatura: FaturaVO): Boolean {
        val cartao = cartaoDomain.buscarCartaoPorId(fatura.cartaoId!!)
        return cartaoDomain.cartaoEstaFechado(
            cartao!!,
            CustomCalendarView.getMonthId(fatura.mes!!)!!,
            fatura.ano!!
        )
    }

    private fun obterDataNovoLancamento(faturaFechada: Boolean): Date {
        val calendar = Calendar.getInstance()
        if (!faturaFechada) {
            calendar.add(Calendar.MONTH, Constantes.UM)
        }
        return calendar.time
    }

    private fun mostrarMensagemESair(mensagem: String, sair: Boolean) {
        Snackbar.make(this.view!!, mensagem, Snackbar.LENGTH_LONG).show()
        if (sair)
            navController.navigateUp()
    }

    private fun init() {
        val cartaoId = arguments?.getLong(Constantes.CARTAO_ID_KEY)
        val mesId = arguments?.getInt(Constantes.MES_KEY)
        val mes = CustomCalendarView.getMonthDescription(mesId!!)
        val ano = arguments?.getInt(Constantes.ANO_KEY)
        val fatura = cartaoDomain.buscarFaturaPorCartaoMesEAno(cartaoId!!, mes!!, ano!!)
        viewModel.fatura.postValue(fatura)
    }

    private fun initChildren(view: View) {
        this.cetValor = view.findViewById(R.id.cet_valor)
        this.btnPagar = view.findViewById(R.id.btn_pagar)
        this.txMesFatura = view.findViewById(R.id.tx_mes_fatura)
        this.btnPagar.setOnClickListener(this)
    }
}
