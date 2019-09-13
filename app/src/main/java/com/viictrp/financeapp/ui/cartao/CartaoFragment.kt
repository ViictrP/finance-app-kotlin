package com.viictrp.financeapp.ui.cartao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.R
import com.viictrp.financeapp.adapter.CartaoAdapter
import com.viictrp.financeapp.adapter.LancamentoAdapter
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.CartaoRepository
import com.viictrp.financeapp.repository.FaturaRepository
import com.viictrp.financeapp.repository.LancamentoRepository
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.ui.custom.CustomCalendarView.OnMonthChangeListener
import com.viictrp.financeapp.ui.custom.LinePagerIndicatorDecoration
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener.OnItemChangedListener
import com.viictrp.financeapp.utils.StatusBarTheme
import com.viictrp.financeapp.utils.SwipeToDeleteCallback

class CartaoFragment : Fragment(), OnClickListener, OnMonthChangeListener, OnItemChangedListener {

    private lateinit var cartaoViewModel: CartaoViewModel
    private lateinit var crCartoes: RecyclerView
    private lateinit var calendarView: CustomCalendarView
    private lateinit var rvLancamentos: RecyclerView

    // Repositories
    private lateinit var lancamentoRepository: LancamentoRepository
    private lateinit var cartaoRepository: CartaoRepository
    private lateinit var faturaRepository: FaturaRepository

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
        cartaoViewModel = ViewModelProviders.of(this).get(CartaoViewModel::class.java)
        initChildren(view)
        initObservers()
        init()
    }

    private fun init() {
        this.lancamentoRepository = LancamentoRepository(this.context!!)
        this.cartaoRepository = CartaoRepository(this.context!!)
        this.faturaRepository = FaturaRepository(this.context!!)
        loadCartoes()
    }

    private fun initObservers() {
        cartaoViewModel.cartoes.observe(this, Observer {
            val adapter = this.crCartoes.adapter as CartaoAdapter
            adapter.setList(it.toMutableList())
        })
    }

    private fun initChildren(root: View) {
        this.crCartoes = root.findViewById(R.id.cr_cartoes)
        this.crCartoes.adapter = CartaoAdapter(mutableListOf(), this.context!!)
        this.crCartoes.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val mSnapHelper = LinearSnapHelper()
        mSnapHelper.attachToRecyclerView(this.crCartoes)
        val snapOnScrollListener = SnapOnScrollListener(
            mSnapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this
        )
        this.crCartoes.addOnScrollListener(snapOnScrollListener)
        this.crCartoes.addItemDecoration(LinePagerIndicatorDecoration())
        this.calendarView = root.findViewById(R.id.calendarView_cartoes)
        this.calendarView.setOnMonthChangeListener(this)
        this.rvLancamentos = root.findViewById(R.id.rv_lancamentos_cartoes)
        this.rvLancamentos.adapter = LancamentoAdapter(mutableListOf(), this.context!!)
        this.rvLancamentos.layoutManager = LinearLayoutManager(this.context!!)
        val swipeHandler = object : SwipeToDeleteCallback(this.context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val adapter = (rvLancamentos.adapter as LancamentoAdapter)
                val lancamento = adapter.getList()!![position]
                deleteLancamento(lancamento)
                adapter.removeAt(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvLancamentos)
    }

    private fun loadCartoes() {
//        val cartoes = cartaoRepository.findCartaoByUsuarioId(Constantes.SYSTEM_USER)
        val cartoes = mutableListOf(Cartao().apply {
            this.limite = 12500.toDouble()
            this.dataFechamento = 10
            this.descricao = "Itaucard Visa Gold"
        }, Cartao().apply {
            this.limite = 8500.toDouble()
            this.dataFechamento = 10
            this.descricao = "Itaucard Tam"
        })
        cartaoViewModel.cartoes.postValue(cartoes)
    }

    fun deleteLancamento(lancamento: Lancamento) {
        lancamentoRepository.delete(lancamento)
        Snackbar.make(
            this.view!!,
            "Lançamento excluído com sucesso.",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onItemChangedListener(position: Int) {
        Snackbar.make(this.view!!, "Position changed $position", Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMonthChange(month: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}