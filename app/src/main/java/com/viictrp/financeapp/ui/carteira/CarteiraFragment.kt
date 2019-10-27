package com.viictrp.financeapp.ui.carteira

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.service.CartaoService
import com.viictrp.financeapp.service.CarteiraService
import com.viictrp.financeapp.service.LancamentoService
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.ui.custom.CustomCalendarView.OnMonthChangeListener
import com.viictrp.financeapp.ui.custom.RialTextView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.utils.NumberOperations
import com.viictrp.financeapp.utils.StatusBarTheme
import com.viictrp.financeapp.utils.SwipeToDeleteCallback
import com.viictrp.financeapp.viewObject.LancamentoVO
import java.util.*

class CarteiraFragment : Fragment(), OnClickListener, OnMonthChangeListener {

    private var navController: NavController? = null
    private lateinit var carteiraViewModel: CarteiraViewModel
    private lateinit var txValorOrcamento: RialTextView
    private lateinit var txGastoAteMomento: RialTextView
    private lateinit var rvLancamentos: RecyclerView
    private lateinit var pbOrcamento: ProgressBar
    private lateinit var calendarView: CustomCalendarView
    private lateinit var txValorDisponivel: RialTextView

    private lateinit var lancamentoService: LancamentoService
    private lateinit var carteiraService: CarteiraService
    private lateinit var cartaoService: CartaoService

    private var lancamentosHolder = mutableListOf<LancamentoVO>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (this.activity!! as MainActivity).disableActionBarButton()
        return inflater.inflate(R.layout.fragment_carteira, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carteiraViewModel = ViewModelProviders.of(this).get(CarteiraViewModel::class.java)
        StatusBarTheme.setLightStatusBar(view, this.activity!!)
        navController = view.findNavController()
        initChildren(view)
        buildModelObservers(view)
        init()
    }

    private fun initChildren(view: View) {
        this.pbOrcamento = view.findViewById(R.id.pb_orcamento)
        this.txValorOrcamento = view.findViewById(R.id.tx_vl_orcamento)
        this.txGastoAteMomento = view.findViewById(R.id.tx_gasto_ate_momento)
        this.txValorDisponivel = view.findViewById(R.id.tx_valor_disponivel)
        this.calendarView = view.findViewById(R.id.calendarView_carteira)
        this.calendarView.setOnMonthChangeListener(this)
        view.findViewById<Button>(R.id.btn_orcamento).setOnClickListener(this)
        view.findViewById<Button>(R.id.btn_novo_lancamento).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_orcamento -> navController!!.navigate(
                R.id.action_navegacao_carteira_to_navegacao_orcamento,
                bundleOf(Constantes.ORCAMENTO_ID_KEY to carteiraViewModel.carteira.value!!.orcamento!!.id),
                null,
                FragmentNavigatorExtras(
                    txValorOrcamento to "orcamento_value"
                )
            )
            R.id.btn_novo_lancamento -> navController!!.navigate(
                R.id.action_navegacao_carteira_to_lancamentoFragment,
                bundleOf(Constantes.CARTEIRA_ID_KEY to carteiraViewModel.carteira.value?.id)
            )
        }
    }

    override fun onMonthChange(month: Int, year: Int) {
        this.loadCarteira(month, year)
    }


    private fun loadCarteira(mes: Int, ano: Int) {
        val carteira = carteiraService.buscarPorMesEAno(mes, ano)
        carteiraViewModel.carteira.postValue(carteira)
        val lancamentos = lancamentoService.buscarLancamentosDaCarteira(carteira!!.id!!, mes, ano)
        this.lancamentosHolder = lancamentos.toMutableList()
        carteiraViewModel.lancamentos.postValue(lancamentos)
        calcularValorTotalGasto(lancamentos, carteira.orcamento!!.valor!!)
    }

    private fun buildModelObservers(root: View) {
        this.rvLancamentos = buildRecyclerView(root)

        carteiraViewModel.lancamentos.observe(this, Observer {
            val adapter = this.rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it.toMutableList())
            val orcamento = carteiraViewModel.carteira.value!!.orcamento
            val valorLancamentos = lancamentoService.calcularValorTotal(it)
            txValorDisponivel.text = "${orcamento!!.valor!! - valorLancamentos}"
        })

        carteiraViewModel.progressBarProgress.observe(this, Observer {
            pbOrcamento.progress = it
        })

        carteiraViewModel.carteira.observe(this, Observer {
            txValorOrcamento.text = "${it.orcamento!!.valor}"
        })
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init() {
        this.calendarView.setMonth(Calendar.getInstance().get(Calendar.MONTH) + 1)
        this.lancamentoService = LancamentoService(this.context!!)
        this.carteiraService = CarteiraService(this.context!!)
        this.cartaoService = CartaoService(this.context!!)
        loadCarteira(
            Calendar.getInstance().get(Calendar.MONTH) + 1,
            Calendar.getInstance().get(Calendar.YEAR)
        )
    }

    private fun calcularValorTotalGasto(lancamentos: List<LancamentoVO>?, valorTotalOrcamento: Double) {
        val calendar = Calendar.getInstance()
        val valorTotal = lancamentoService.calcularValorTotal(lancamentos!!)
        val valorTotalCartoes = obterValorTotalFaturasEmGeral(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        val valor = valorTotal + valorTotalCartoes
        this.txGastoAteMomento.text = "$valor"
        calcularPorcentagemProgressBar(valor, valorTotalOrcamento)
    }

    private fun calcularPorcentagemProgressBar(valorTotal: Double, valorTotalOrcamento: Double) {
        val progress = NumberOperations.getPercentFrom(valorTotal, valorTotalOrcamento)
        this.carteiraViewModel.progressBarProgress.postValue(progress.toInt())
    }

    private fun buildRecyclerView(root: View): RecyclerView {
        val context = this.context!!
        val rvLancamentos: RecyclerView = root.findViewById(R.id.rv_lancamentos)
        rvLancamentos.adapter = LancamentoAdapter(mutableListOf(), context)
        rvLancamentos.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val adapter = (rvLancamentos.adapter as LancamentoAdapter)
                val lancamento = adapter.getList()!![position]
                removerLancamentoPorId(lancamento.id!!)
                adapter.removeAt(position)
                calcularValorTotalGasto(
                    adapter.getList(),
                    carteiraViewModel.carteira.value!!.orcamento!!.valor!!
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvLancamentos)

        return rvLancamentos
    }

    fun removerLancamentoPorId(lancamentoId: Long) {
        lancamentoService.removerLancamentoPorId(lancamentoId)
        Snackbar.make(
            this.view!!,
            "Lançamento excluído com sucesso.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun obterValorTotalFaturasEmGeral(mesId: Int, ano: Int): Double {
        var valorTotal = 0.0
        val cartoes = cartaoService.buscarCartaoPorUsuario(Constantes.SYSTEM_USER)
        val mes = CustomCalendarView.getMonthDescription(mesId)!!
        cartoes.forEach {
            val fatura = cartaoService.buscarFaturaPorCartaoMesEAno(it.id!!, mes, ano)
            fatura?.let { f ->
                val lancamentos = lancamentoService.buscarLancamentosDaFatura(f.id!!)
                val valorLancamentos = lancamentoService.calcularValorTotal(lancamentos)
                valorTotal += valorLancamentos
                val lancamento = LancamentoVO().apply {
                    this.numeroParcela = 1
                    this.quantidadeParcelas = 1
                    this.valor = valorLancamentos
                    this.carteiraId = carteiraViewModel.carteira.value?.id
                    this.data = Date()
                    this.descricao = "Fatura do cartão ${it.descricao}"
                    this.titulo = "${it.descricao}"
                }
                lancamentosHolder.add(lancamento)
            }

        }
        carteiraViewModel.lancamentos.postValue(lancamentosHolder)
        return valorTotal
    }

}
