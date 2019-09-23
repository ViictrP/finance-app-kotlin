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
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.repository.CarteiraRepository
import com.viictrp.financeapp.repository.LancamentoRepository
import com.viictrp.financeapp.repository.OrcamentoRepository
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.ui.custom.CustomCalendarView.OnMonthChangeListener
import com.viictrp.financeapp.ui.custom.RialTextView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.utils.StatusBarTheme
import com.viictrp.financeapp.utils.SwipeToDeleteCallback
import java.util.*

class CarteiraFragment : Fragment(), OnClickListener, OnMonthChangeListener {

    private var navController: NavController? = null
    private lateinit var carteiraViewModel: CarteiraViewModel
    private lateinit var txValorOrcamento: RialTextView
    private lateinit var txGastoAteMomento: TextView
    private lateinit var rvLancamentos: RecyclerView
    private lateinit var pbOrcamento: ProgressBar
    private lateinit var calendarView: CustomCalendarView
    private lateinit var txValorDisponivel: RialTextView

    private lateinit var lancamentoRepository: LancamentoRepository
    private lateinit var carteiraRepository: CarteiraRepository
    private lateinit var orcamentoRepository: OrcamentoRepository

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
        this.pbOrcamento = view.findViewById(R.id.pb_orcamento)
        this.txValorOrcamento = view.findViewById(R.id.tx_vl_orcamento)
        this.txGastoAteMomento = view.findViewById(R.id.tx_gasto_ate_momento)
        this.txValorDisponivel = view.findViewById(R.id.tx_valor_disponivel)
        this.calendarView = view.findViewById(R.id.calendarView_carteira)
        this.calendarView.setOnMonthChangeListener(this)
        view.findViewById<Button>(R.id.btn_orcamento).setOnClickListener(this)
        view.findViewById<Button>(R.id.btn_novo_lancamento).setOnClickListener(this)
        buildModelObservers(view)
        init()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_orcamento -> navController!!.navigate(
                R.id.action_navegacao_carteira_to_navegacao_orcamento,
                bundleOf(Constantes.ORCAMENTO_ID_KEY to carteiraViewModel.orcamento.value?.id),
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
        val mes = CustomCalendarView.getMonthDescription(month)
        this.loadCarteira(mes!!)
    }

    /**
     * Inicializa os observers
     */
    private fun buildModelObservers(root: View) {
        this.rvLancamentos = buildRecyclerView(root)

        carteiraViewModel.orcamento.observe(this, Observer {
            txValorOrcamento.text = "${it.valor}"
        })

        carteiraViewModel.lancamentos.observe(this, Observer {
            val adapter = this.rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it.toMutableList())
            if (it.isNotEmpty()) {
                carteiraViewModel.orcamento.value.let { orcamento ->
                    val valorLancamentos =
                        it.map { lancamento -> lancamento.valor!! }.reduce { soma, lanc -> soma + lanc }
                    txValorDisponivel.text = "${orcamento!!.valor!! - valorLancamentos}"
                }
            }
        })

        carteiraViewModel.progressBarProgress.observe(this, Observer {
            pbOrcamento.progress = it
        })

        carteiraViewModel.carteira.observe(this, Observer(System.out::println))
    }

    /**
     * Busca a carteira do mês
     */
    private fun loadCarteira(mes: String) {
        val carteira = this.carteiraRepository.findCarteiraByMes(mes)
        if (carteira != null) {
            loadOrcamento(carteira)
            carteiraViewModel.carteira.postValue(carteira)
        } else {
            criarNovaCarteira(mes)
        }
    }

    /**
     * Inicializa os campos da tela
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init() {
        val monthId = Calendar.getInstance().get(Calendar.MONTH) + 1
        this.calendarView.setMonth(monthId)
        val mes = CustomCalendarView.getMonthDescription(monthId)
        this.lancamentoRepository = LancamentoRepository(this.context!!)
        this.carteiraRepository = CarteiraRepository(this.context!!)
        this.orcamentoRepository = OrcamentoRepository(this.context!!)
        loadCarteira(mes!!)
    }

    /**
     * Busca todos os lançamentos da carteira
     *
     * @param carteiraId Id da carteiraLong
     */
    private fun loadLancamentos(carteiraId: Long, valorOrcamento: Double) {
        val lancamentos = lancamentoRepository.findLancamentosByCarteiraId(carteiraId)

        calcularValorTotalGasto(lancamentos, valorOrcamento)
        carteiraViewModel.lancamentos.postValue(lancamentos)
    }

    /**
     * Calcula o valor total de lançamentos para setar o progresso do pnOrcamento.
     *
     * @param lancamentos - lançamentos da carteira
     * @param valorTotalOrcamento - valor do orçamento do mês
     */
    private fun calcularValorTotalGasto(
        lancamentos: List<Lancamento>?,
        valorTotalOrcamento: Double
    ) {
        var valorTotal = 0.0
        lancamentos?.forEach {
            it.valor.let { value ->
                valorTotal += value!!
            }
        }
        this.txGastoAteMomento.text =
            "$valorTotal".substring(0, valorTotal.toString().indexOf(".") + 2)
        calcularPorcentagemProgressBar(valorTotal, valorTotalOrcamento)
    }

    /**
     * Calcula o progresso do pbOrcamento de acordo com o valor total dos lançamentos
     * e o valor do orçamento do mês
     *
     * @param valorTotal - valor total dos lançamentos
     * @param valorTotalOrcamento - valor total do orçamento do mês
     */
    private fun calcularPorcentagemProgressBar(valorTotal: Double, valorTotalOrcamento: Double) {
        val progress =
            if (valorTotal >= valorTotalOrcamento) 100
            else ((valorTotal / valorTotalOrcamento) * 100).toInt()
        this.carteiraViewModel.progressBarProgress.postValue(progress)
    }

    /**
     * Carrega o orçamento da carteira
     *
     * @param carteira carteira
     */
    private fun loadOrcamento(carteira: Carteira) {
        val orcamento = orcamentoRepository.findOrcamentoByCarteiraId(carteira.id!!)
        if (orcamento == null) {
            criarNovoOrcamento(carteira.id, carteira.mes)
        } else {
            carteiraViewModel.orcamento.postValue(orcamento)
            loadLancamentos(carteira.id!!, orcamento.valor!!)
        }
    }

    /**
     * Cria um novo orçamento caso não exista na carteira
     */
    private fun criarNovoOrcamento(carteiraId: Long?, mes: String?) {
        val orcamento = Orcamento(0.0, mes)
        orcamento.carteiraId = carteiraId
        orcamentoRepository.save(orcamento) {
            this.activity!!.runOnUiThread {
                carteiraViewModel.orcamento.postValue(it)
            }
        }
    }

    /**
     * Cria uma nova carteira caso não exista carteiras para o mês atual
     */
    private fun criarNovaCarteira(mes: String) {
        val carteira = Carteira(mes).apply {
            this.usuarioId = 1
        }
        carteiraRepository.save(carteira) {
            criarNovoOrcamento(carteira.id, carteira.mes)
            this.activity!!.runOnUiThread {
                carteiraViewModel.carteira.postValue(it)
                carteiraViewModel.lancamentos.postValue(mutableListOf())
            }
        }
    }

    /**
     * Construindo a RecyclerView para listar os lançamentos
     */
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
                deleteLancamento(lancamento)
                adapter.removeAt(position)
                calcularValorTotalGasto(
                    adapter.getList(),
                    carteiraViewModel.orcamento.value!!.valor!!
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvLancamentos)

        return rvLancamentos
    }

    fun deleteLancamento(lancamento: Lancamento) {
        lancamentoRepository.delete(lancamento)
        Snackbar.make(
            this.view!!,
            "Lançamento excluído com sucesso.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

}
