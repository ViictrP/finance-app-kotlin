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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.realm.RealmInitializer
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.ui.custom.CustomCalendarView.OnMonthChangeListener
import com.viictrp.financeapp.ui.custom.RialTextView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.utils.StatusBarTheme
import io.realm.kotlin.where
import java.util.*

class CarteiraFragment : Fragment(), OnClickListener, OnMonthChangeListener {

    private var navController: NavController? = null
    private lateinit var carteiraViewModel: CarteiraViewModel
    private lateinit var txValorOrcamento: RialTextView
    private lateinit var txGastoAteMomento: TextView
    private lateinit var rvLancamentos: RecyclerView
    private lateinit var pbOrcamento: ProgressBar
    private lateinit var calendarView: CustomCalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        this.calendarView = view.findViewById(R.id.calendarView)
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
                bundleOf(Constantes.orcamentoIdKey to carteiraViewModel.orcamento.value?.id),
                null,
                FragmentNavigatorExtras(
                    txValorOrcamento to "orcamento_value"
                )
            )
            R.id.btn_novo_lancamento -> navController!!.navigate(
                R.id.action_navegacao_carteira_to_lancamentoFragment,
                bundleOf(Constantes.carteiraIdKey to carteiraViewModel.carteira.value?.id)
            )
        }
    }

    override fun onMonthChange(month: Int) {
        val mes = this.calendarView.getMonthDescription(month)
        loadCarteira(mes!!)
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
            adapter.setList(it)
            adapter.notifyDataSetChanged()
            //TODO atualizar progressbar e porcentagem
        })

        carteiraViewModel.carteira.observe(this, Observer(System.out::println))
    }

    /**
     * Inicializa os campos da tela
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init() {
        val mes = this.calendarView.getMonthDescription(Calendar.getInstance().get(Calendar.MONTH))
        loadCarteira(mes!!)
    }

    /**
     * Busca a carteira do mês
     */
    private fun loadCarteira(mes: String) {
        val realm = RealmInitializer.getInstance(this.context!!)
        val carteira = realm.where<Carteira>().equalTo(Constantes.mes, mes).findFirst()
        if (carteira != null) {
            loadOrcamento(carteira)
            carteiraViewModel.carteira.postValue(carteira)
        } else {
            criarNovaCarteira(mes)
        }
    }

    /**
     * Busca todos os lançamentos da carteira
     *
     * @param carteiraId Id da carteiraLong
     */
    private fun loadLancamentos(carteiraId: Long, valorOrcamento: Double) {
        val realm = RealmInitializer.getInstance(this.context!!)
        val lancamentos = realm.where<Lancamento>()
            .equalTo(Constantes.carteiraId, carteiraId)
            .findAll()

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
        this.pbOrcamento.progress =
            if (valorTotal >= valorTotalOrcamento) 100
            else ((valorTotal / valorTotalOrcamento) * 100).toInt()
    }

    /**
     * Carrega o orçamento da carteira
     *
     * @param carteira carteira
     */
    private fun loadOrcamento(carteira: Carteira) {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        val orcamento =
            realm.where<Orcamento>().equalTo(Constantes.carteiraId, carteira.id).findFirst()
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
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        realm.executeTransactionAsync {
            val lastId = it.where<Orcamento>().max(Constantes.id)
            val orcamento = Orcamento(0.0, mes)
            orcamento.carteiraId = carteiraId
            if (lastId != null) orcamento.id = lastId.toLong() + 1 else orcamento.id = 1
            it.insert(orcamento)
            this.activity!!.runOnUiThread(Runnable {
                carteiraViewModel.orcamento.postValue(orcamento)
            })
        }
    }

    /**
     * Cria uma nova carteira caso não exista carteiras para o mês atual
     */
    private fun criarNovaCarteira(mes: String) {
        val realm = RealmInitializer.getInstance(this.activity!!.applicationContext)
        realm.executeTransactionAsync {
            val newCarteira = Carteira(mes)
            val lastId = it.where<Carteira>().max(Constantes.id)
            if (lastId != null) newCarteira.id = lastId.toLong() + 1 else newCarteira.id = 1
            newCarteira.usuarioId = 1
            it.insert(newCarteira)
            criarNovoOrcamento(newCarteira.id, newCarteira.mes)
            this.activity!!.runOnUiThread(Runnable {
                carteiraViewModel.carteira.postValue(newCarteira)
            })
        }
    }

    /**
     * Construindo a RecyclerView para listar os lançamentos
     */
    private fun buildRecyclerView(root: View): RecyclerView {
        val context = this.activity!!.applicationContext
        val rvLancamentos: RecyclerView = root.findViewById(R.id.rv_lancamentos)
        rvLancamentos.adapter = LancamentoAdapter(listOf(), context)
        rvLancamentos.layoutManager = LinearLayoutManager(context)
        return rvLancamentos
    }

}
