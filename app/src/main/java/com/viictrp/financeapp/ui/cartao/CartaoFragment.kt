package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.CartaoAdapter
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.service.CartaoService
import com.viictrp.financeapp.service.LancamentoService
import com.viictrp.financeapp.ui.custom.CirclePagerIndicatorDecoration
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.ui.custom.CustomCalendarView.OnMonthChangeListener
import com.viictrp.financeapp.ui.custom.RialTextView
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener.OnItemChangedListener
import com.viictrp.financeapp.utils.*
import com.viictrp.financeapp.viewObject.LancamentoVO
import java.util.*

class CartaoFragment : Fragment(), OnClickListener, OnMonthChangeListener, OnItemChangedListener {

    private lateinit var navController: NavController

    // Domains
    private lateinit var lancamentoService: LancamentoService

    private lateinit var cartaoViewModel: CartaoViewModel
    private lateinit var cartaoService: CartaoService

    // Screen components
    private lateinit var crCartoes: RecyclerView
    private lateinit var calendarView: CustomCalendarView
    private lateinit var rvLancamentos: RecyclerView
    private lateinit var txCartaoValorFatura: RialTextView
    private lateinit var txCartaoDescricao: TextView
    private lateinit var btnPagarFatura: Button
    private lateinit var btnNovoLancamento: Button
    private lateinit var txPagoValor: RialTextView
    private lateinit var txPago: TextView
    private lateinit var pbLimite: ProgressBar
    private lateinit var txValorDisponivel: RialTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cartao, container, false)
        StatusBarTheme.setLightStatusBar(root, this.activity!!)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        (this.activity!! as MainActivity).displayActionBarButton(
            R.drawable.ic_credit_card_with_plus_sign_24,
            this
        )
        this.txCartaoValorFatura = view.findViewById(R.id.tx_valor_fatura)
        this.txCartaoDescricao = view.findViewById(R.id.tx_descricao_cartao)
        cartaoViewModel = ViewModelProviders.of(this).get(CartaoViewModel::class.java)
        initChildren(view)
        initObservers()
        init()
    }

    override fun onItemChangedListener(position: Int) {
        cartaoViewModel.cartoes.value.let {
            it.let { cartoes ->
                val cartao = cartoes!![position]
                cartaoViewModel.cartaoSelecionado.postValue(cartao)
                buscarLancamentosCartao(
                    cartao.id!!,
                    cartaoViewModel.mesSelecionado.value!!,
                    cartaoViewModel.anoSelecionado.value!!
                )
            }
        }
    }

    override fun onClick(button: View?) {
        when (button!!.id) {
            R.id.action_bar_button -> navController.navigate(
                R.id.action_navegacao_cartao_to_gerenciarCartao
            )
            R.id.btn_novo_lancamento -> navController.navigate(
                R.id.action_navegacao_cartao_to_lancamentoFragment,
                bundleOf(Constantes.CARTAO_ID_KEY to cartaoViewModel.cartaoSelecionado.value?.id)
            )
            R.id.btn_pagar_cartao -> navController.navigate(
                R.id.action_navegacao_cartao_to_gerenciarFaturaFragment,
                bundleOf(
                    Constantes.CARTAO_ID_KEY to cartaoViewModel.cartaoSelecionado.value?.id,
                    Constantes.MES_KEY to cartaoViewModel.mesSelecionado.value,
                    Constantes.ANO_KEY to cartaoViewModel.anoSelecionado.value
                )
            )
        }
    }

    private fun desabilitarBotaoPagarFatura(faturaFechada: Boolean, mesId: Int, ano: Int) {
        val mes = CustomCalendarView.getMonthDescription(mesId)
        cartaoViewModel.cartaoSelecionado.value.let {
            if (it == null) this.btnPagarFatura.isEnabled = false else {
                val fatura = cartaoService.buscarFaturaPorCartaoMesEAno(it.id!!, mes!!, ano)
                val pago = if (fatura != null) fatura.pago!! else false
                this.btnPagarFatura.isEnabled = faturaFechada && !pago
                this.btnNovoLancamento.isEnabled = !faturaFechada
                if (pago) {
                    val pagamentos =
                        cartaoService.buscarPagamentosFaturaPorMesAndAno(fatura!!.id!!, mesId, ano)
                    if (pagamentos.isNotEmpty()) {
                        val valor = pagamentos.map { pagamento -> pagamento.valor!! }
                            .reduce { soma, next -> soma + next }
                        this.txPagoValor.text = "$valor"
                        this.txPago.visibility = View.VISIBLE
                        this.txPagoValor.visibility = View.VISIBLE
                    }
                } else {
                    this.txPago.visibility = View.GONE
                    this.txPagoValor.visibility = View.GONE
                }
            }
        }
    }

    override fun onMonthChange(month: Int, year: Int) {
        cartaoViewModel.cartaoSelecionado.value.let { cartao ->
            if (cartao != null) buscarLancamentosCartao(cartao.id!!, month, year)
            cartaoViewModel.mesSelecionado.postValue(month)
            cartaoViewModel.anoSelecionado.postValue(year)
            val faturaFechada = cartaoService.cartaoEstaFechado(cartao!!, month, year)
            desabilitarBotaoPagarFatura(faturaFechada, month, year)
        }
    }

    private fun init() {
        val monthId = Calendar.getInstance().get(Calendar.MONTH) + 1
        this.calendarView.setMonth(monthId)
        this.lancamentoService = LancamentoService(this.context!!)
        this.cartaoService = CartaoService(this.context!!)
        this.cartaoViewModel.mesSelecionado.postValue(Calendar.getInstance().get(Calendar.MONTH) + 1)
        this.cartaoViewModel.anoSelecionado.postValue(Calendar.getInstance().get(Calendar.YEAR))
        buscarCartoes()
    }

    private fun initObservers() {
        cartaoViewModel.cartoes.observe(this, Observer {
            val adapter = this.crCartoes.adapter as CartaoAdapter
            if (it.size > Constantes.ZERO) {
                this.crCartoes.scrollToPosition(Constantes.ZERO)
                onItemChangedListener(Constantes.ZERO)
            }
            adapter.setList(it.toMutableList())
        })

        cartaoViewModel.lancamentos.observe(this, Observer {
            this.txCartaoValorFatura.text = lancamentoService.calcularValorTotal(it).toString()
            val adapter = this.rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it.toMutableList())
            val limite = cartaoService.calcularLimiteDisponivel(this.cartaoViewModel.cartaoSelecionado.value!!, it)
            val valorTotalParcelado = lancamentoService.calcularValorTotalComprasParceladas(it)
            this.txValorDisponivel.text = (limite - valorTotalParcelado).toString()
        })

        cartaoViewModel.cartaoSelecionado.observe(this, Observer {
            this.txCartaoDescricao.text = it.descricao
            val mes = cartaoViewModel.mesSelecionado.value!!
            val ano = cartaoViewModel.anoSelecionado.value!!
            val faturaFechada = cartaoService.cartaoEstaFechado(it, mes, ano)
            desabilitarBotaoPagarFatura(faturaFechada, mes, ano)
        })

        cartaoViewModel.valorDisponivel.observe(this, Observer {
            this.txValorDisponivel.text = it.toString()
        })

        cartaoViewModel.limitePercent.observe(this, Observer {
            this.pbLimite.progress = it
        })
    }

    private fun initChildren(root: View) {
        this.btnNovoLancamento = root.findViewById(R.id.btn_novo_lancamento)
        this.btnNovoLancamento.setOnClickListener(this)
        this.btnPagarFatura = root.findViewById(R.id.btn_pagar_cartao)
        this.btnPagarFatura.setOnClickListener(this)
        this.calendarView = root.findViewById(R.id.calendarView_cartoes)
        this.calendarView.setOnMonthChangeListener(this)
        this.txPagoValor = root.findViewById(R.id.tx_pago_valor)
        this.txPago = root.findViewById(R.id.tx_pago)
        this.pbLimite = root.findViewById(R.id.pb_limite)
        this.txValorDisponivel = root.findViewById(R.id.tx_valor_disponivel)
        buildCrCartoes(root)
        buildRVLancamentos(root)
    }

    private fun buscarCartoes() {
        val cartoes = cartaoService.buscarCartaoPorUsuario(Constantes.SYSTEM_USER)
        cartaoViewModel.cartoes.postValue(cartoes)
    }

    private fun deleteLancamento(lancamento: LancamentoVO) {
        lancamentoService.removerLancamentoPorId(lancamento.id!!)
        Snackbar.make(
            this.view!!,
            "Lançamento excluído com sucesso.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun buildRVLancamentos(root: View) {
        this.rvLancamentos = RecyclerViewBuilder()
            .forRecyclerView(root.findViewById(R.id.rv_lancamentos_cartoes))
            .withLayoutManager(LinearLayoutManager(this.context!!))
            .withAdapter(LancamentoAdapter(mutableListOf(), this.context!!))
            .withSwipeHandler(
                object : SwipeToDeleteCallback(this.context!!) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val adapter = (rvLancamentos.adapter as LancamentoAdapter)
                        val lancamento = adapter.getList()!![position]
                        deleteLancamento(lancamento)
                        adapter.removeAt(position)
                    }
                }
            ).build()
    }

    private fun buildCrCartoes(root: View) {
        this.crCartoes = CarouselBuilder()
            .convertToCarousel(root.findViewById(R.id.cr_cartoes), this.context!!, this)
            .withDecoration(CirclePagerIndicatorDecoration())
            .withAdapter(CartaoAdapter(mutableListOf(), this.context!!))
            .build()
    }

    private fun buscarLancamentosCartao(cartaoId: Long, mes: Int, ano: Int) {
        val month = CustomCalendarView.getMonthDescription(mes)
        val fatura = cartaoService.buscarFaturaPorCartaoMesEAno(cartaoId, month!!, ano)
        val lancamentos = lancamentoService.buscarLancamentosDaFatura(fatura!!.id!!)
        cartaoViewModel.lancamentos.postValue(lancamentos)
    }
}
