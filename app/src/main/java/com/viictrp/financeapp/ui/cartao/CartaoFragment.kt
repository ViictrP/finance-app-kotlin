package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
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
import com.viictrp.financeapp.domain.CartaoDomain
import com.viictrp.financeapp.domain.LancamentoDomain
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.ui.custom.CirclePagerIndicatorDecoration
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.ui.custom.CustomCalendarView.OnMonthChangeListener
import com.viictrp.financeapp.ui.custom.RialTextView
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener.OnItemChangedListener
import com.viictrp.financeapp.utils.*
import java.util.*

class CartaoFragment : Fragment(), OnClickListener, OnMonthChangeListener, OnItemChangedListener {

    private lateinit var navController: NavController

    private lateinit var cartaoViewModel: CartaoViewModel

    // Domains
    private lateinit var lancamentoDomain: LancamentoDomain
    private lateinit var cartaoDomain: CartaoDomain

    // Screen components
    private lateinit var crCartoes: RecyclerView
    private lateinit var calendarView: CustomCalendarView
    private lateinit var rvLancamentos: RecyclerView
    private lateinit var txCartaoValorFatura: RialTextView
    private lateinit var txCartaoDescricao: TextView

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
        }
    }

    override fun onMonthChange(month: Int, year: Int) {
        cartaoViewModel.cartaoSelecionado.value.let { cartao ->
            if (cartao != null) buscarLancamentosCartao(cartao.id!!, month, year)
            cartaoViewModel.mesSelecionado.postValue(month)
            cartaoViewModel.anoSelecionado.postValue(year)
        }
    }

    private fun init() {
        val monthId = Calendar.getInstance().get(Calendar.MONTH) + 1
        this.calendarView.setMonth(monthId)
        this.lancamentoDomain = LancamentoDomain(this.context!!)
        this.cartaoDomain = CartaoDomain(this.context!!)
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
            this.txCartaoValorFatura.text = lancamentoDomain.calcularValorTotal(it).toString()
            val adapter = this.rvLancamentos.adapter as LancamentoAdapter
            adapter.setList(it.toMutableList())
        })

        cartaoViewModel.cartaoSelecionado.observe(this, Observer {
            this.txCartaoDescricao.text = it.descricao
        })
    }

    private fun initChildren(root: View) {
        root.findViewById<Button>(R.id.btn_novo_lancamento).setOnClickListener(this)
        this.calendarView = root.findViewById(R.id.calendarView_cartoes)
        this.calendarView.setOnMonthChangeListener(this)
        buildCrCartoes(root)
        buildRVLancamentos(root)
    }

    private fun buscarCartoes() {
        val cartoes = cartaoDomain.buscarCartaoPorUsuario(Constantes.SYSTEM_USER)
        cartaoViewModel.cartoes.postValue(cartoes)
    }

    private fun deleteLancamento(lancamento: Lancamento) {
        lancamentoDomain.removerLancamento(lancamento)
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
        val fatura = cartaoDomain.buscarFaturaPorCartaoMesEAno(cartaoId, month!!, ano)
        val lancamentos = lancamentoDomain.buscarLancamentosDaFatura(fatura!!.id!!)
        cartaoViewModel.lancamentos.postValue(lancamentos)
    }
}