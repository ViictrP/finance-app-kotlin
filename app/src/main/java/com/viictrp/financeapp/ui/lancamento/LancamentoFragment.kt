package com.viictrp.financeapp.ui.lancamento

import android.app.DatePickerDialog
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
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.CarteiraRepository
import com.viictrp.financeapp.repository.FaturaRepository
import com.viictrp.financeapp.repository.LancamentoRepository
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import java.util.*
import java.text.SimpleDateFormat

class LancamentoFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: LancamentoViewModel

    // NavController
    private var navController: NavController? = null

    // EditTexts
    private var etTitulo: EditText? = null
    private var etDescricao: EditText? = null
    private var etValor: CurrencyEditText? = null
    private var etData: EditText? = null
    private var etQtParcelas: EditText? = null

    private lateinit var lancamentoRepository: LancamentoRepository
    private lateinit var carteiraRepository: CarteiraRepository
    private lateinit var faturaRepository: FaturaRepository

    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (this.activity!! as MainActivity).disableActionBarButton()
        return inflater.inflate(R.layout.fragment_lancamento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.navController = view.findNavController()
        viewModel = ViewModelProviders.of(this).get(LancamentoViewModel::class.java)
        this.lancamentoRepository = LancamentoRepository(this.context!!)
        this.carteiraRepository = CarteiraRepository(this.context!!)
        this.faturaRepository = FaturaRepository(this.context!!)
        bindEditTexts(view)
        bindObservers()
        view.findViewById<CardView>(R.id.btn_salvar).setOnClickListener(this)
        viewModel.carteiraId.postValue(arguments?.getLong(Constantes.CARTEIRA_ID_KEY))
        viewModel.faturaId.postValue(arguments?.getLong(Constantes.FATURA_ID_KEY))
    }

    override fun onClick(view: View?) {
        val lancamento = getLancamento()
        validarCarteiraOuFatura(lancamento)
        lancamentoRepository.save(lancamento) {
            val monthId = lancamento.data!!.split("/")[Constantes.UM]
            val mes = CustomCalendarView.getMonthDescription(monthId.toInt())
            val msg = if (it!!.carteiraId != null) "Lançamento criado na carteira do mês de $mes"
            else "Lançamento criado na fatura do mês de $mes"
            Snackbar.make(this.view!!, msg, Snackbar.LENGTH_LONG)
                .show()
            navController?.navigateUp()
        }
    }

    private fun validarCarteiraOuFatura(lancamento: Lancamento) {
        validarCarteira(lancamento)
        validarFatura(lancamento)
    }

    private fun validarFatura(lancamento: Lancamento) {
        viewModel.faturaId.value.let {
            if (it != null && it != Constantes.ZERO_LONG) {
                setarFaturaId(it, lancamento)
            }
        }
    }

    private fun setarFaturaId(it: Long, lancamento: Lancamento) {
        val fatura = faturaRepository.findById(it)
        // TODO pegar o mês do lançamento ao invés do ID da fatura
        if (fatura != null) {
            val diaLancamento = lancamento.data!!.split("/")[Constantes.ZERO]
            if (fatura.diaFechamento!! > diaLancamento.toLong()) {
                lancamento.faturaId = fatura.id
            } else {
                setarFaturaIdProximaFatura(fatura, lancamento)
            }
        }
    }

    private fun setarFaturaIdProximaFatura(fatura: Fatura, lancamento: Lancamento) {
        val nextMonth = CustomCalendarView.getNextMonth(fatura.mes!!)
        var proximaFatura =
            faturaRepository.findByCartaoIdAndMes(fatura.cartaoId!!, nextMonth!!)
        if (proximaFatura != null) lancamento.faturaId = proximaFatura.id!!
        else {
            proximaFatura = criarProximaFatura(fatura, nextMonth)
            lancamento.faturaId = proximaFatura.id!!
        }
    }

    private fun criarProximaFatura(
        fatura: Fatura,
        nextMonth: String?
    ): Fatura {
        val proximaFatura = Fatura().apply {
            this.usuarioId = fatura.usuarioId
            this.titulo = "Fatura do mês de $nextMonth"
            this.pago = false
            this.mes = nextMonth
            this.diaFechamento = fatura.diaFechamento
            this.descricao = "Fatura do mês de $nextMonth"
            this.cartaoId = fatura.cartaoId
        }
        faturaRepository.save(proximaFatura)
        return proximaFatura
    }

    private fun validarCarteira(lancamento: Lancamento) {
        viewModel.carteiraId.value.let {
            if (it != null && it != Constantes.ZERO_LONG) {
                val carteira = carteiraRepository.findById(it)
                if (carteira != null) lancamento.carteiraId = carteira.id!!
            }
        }
    }

    private fun getLancamento(): Lancamento {
        val lancamento = Lancamento()
        lancamento.titulo = this.etTitulo!!.text.toString()
        lancamento.descricao = this.etDescricao!!.text.toString()
        lancamento.valor = this.etValor!!.currencyDouble
        lancamento.data = this.etData!!.text.toString()
        lancamento.quantidadeParcelas = this.etQtParcelas!!.text.toString().toInt()
        return lancamento
    }

    /**
     * Preenche os EditTexts da tela
     */
    private fun bindEditTexts(view: View) {
        this.etTitulo = view.findViewById(R.id.et_lancamento_titulo)
        this.etDescricao = view.findViewById(R.id.et_lancamento_descricao)
        this.etValor = view.findViewById(R.id.et_lancamento_valor)
        this.etData = view.findViewById(R.id.et_lancamento_data)
        this.etQtParcelas = view.findViewById(R.id.et_lancamento_qt_parcelas)
        val dpDialog = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            this.calendar.set(Calendar.YEAR, year)
            this.calendar.set(Calendar.MONTH, monthOfYear)
            this.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            this.updateEtData()
        }
        this.etData!!.setOnFocusChangeListener { _, enter ->
            if (enter)
                DatePickerDialog(
                    this.context!!,
                    dpDialog,
                    this.calendar.get(Calendar.YEAR),
                    this.calendar.get(Calendar.MONTH),
                    this.calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
        }
    }

    /**
     * Inicializa os observers do viewmodel
     */
    private fun bindObservers() {
        viewModel.titulo.observe(this, Observer {
            this.etTitulo?.setText(it)
        })
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

    /**
     * Atualiza o valor do editText etData quando uma data for escolhida
     * no DatePickerDialog
     */
    private fun updateEtData() {

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale("pt", "BR"))

        this.etData!!.setText(sdf.format(this.calendar.time))
    }
}
