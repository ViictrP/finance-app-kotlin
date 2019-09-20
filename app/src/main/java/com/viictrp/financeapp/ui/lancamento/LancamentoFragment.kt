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
        viewModel.cartaoId.postValue(arguments?.getLong(Constantes.CARTAO_ID_KEY))
    }

    override fun onClick(view: View?) {
        val lancamento = getLancamento()
        salvarNaCarteiraOuFatura(lancamento)
    }

    private fun salvarNaCarteiraOuFatura(lancamento: Lancamento) {
        salvarNaCarteira(lancamento)
        salvarNaFatura(lancamento)
    }

    private fun salvarNaFatura(lancamento: Lancamento) {
        viewModel.cartaoId.value.let {
            if (it != null && it != Constantes.ZERO_LONG) {
                val dataArr = lancamento.data!!.split("/")
                val diaLancamento = dataArr[Constantes.ZERO]
                val mesLancamento = dataArr[Constantes.UM]
                val anoLancamento = dataArr[Constantes.DOIS]
                val fatura = buscarFatura(
                    it,
                    mesLancamento.toInt(),
                    diaLancamento.toLong(),
                    anoLancamento.toInt()
                )
                if (fatura != null) {
                    lancamento.faturaId = fatura.id
                    lancamentoRepository.save(lancamento) {
                        showMessageAndExit("Lançamento criado na fatura de ${fatura.mes}")
                    }
                } else {
                    showMessageAndExit("A fatura não existe")
                }
            }
        }
    }

    private fun buscarFatura(cartaoId: Long, mes: Int, diaLancamento: Long, ano: Int): Fatura? {
        val fatura = faturaRepository.findByCartaoIdAndMesAndAno(
            cartaoId,
            CustomCalendarView.getMonthDescription(mes)!!,
            ano
        )
        if (fatura != null) {
            return if (fatura.diaFechamento!! > diaLancamento) fatura
            else {
                buscarProximaFatura(fatura)
            }
        }
        return null
    }

    private fun buscarProximaFatura(fatura: Fatura): Fatura {
        val nextMonth = CustomCalendarView.getNextMonth(fatura.mes!!)
        var ano = fatura.ano!!
        if (CustomCalendarView.JANEIRO == CustomCalendarView.getMonthId(nextMonth!!)) {
            ano += 1
        }
        var proximaFatura =
            faturaRepository.findByCartaoIdAndMesAndAno(fatura.cartaoId!!, nextMonth, ano)
        if (proximaFatura == null) proximaFatura = criarProximaFatura(fatura, nextMonth, ano)
        return proximaFatura
    }

    private fun criarProximaFatura(
        fatura: Fatura,
        nextMonth: String?,
        ano: Int
    ): Fatura {
        val proximaFatura = Fatura().apply {
            this.usuarioId = fatura.usuarioId
            this.titulo = "Fatura do mês de $nextMonth"
            this.pago = false
            this.mes = nextMonth
            this.ano = ano
            this.diaFechamento = fatura.diaFechamento
            this.descricao = "Fatura do mês de $nextMonth"
            this.cartaoId = fatura.cartaoId
        }
        faturaRepository.save(proximaFatura)
        return proximaFatura
    }

    private fun salvarNaCarteira(lancamento: Lancamento) {
        viewModel.carteiraId.value.let {
            if (it != null && it != Constantes.ZERO_LONG) {
                val carteira = carteiraRepository.findById(it)
                if (carteira != null) {
                    lancamento.carteiraId = carteira.id!!
                    lancamentoRepository.save(lancamento) {
                        showMessageAndExit("Lançamento criado na carteira de ${carteira.mes}")
                    }
                } else {
                    showMessageAndExit("A carteira não existe para o código $it")
                }
            }
        }
    }

    private fun showMessageAndExit(message: String) {
        Snackbar.make(
            this.view!!,
            message,
            Snackbar.LENGTH_LONG
        ).show()
        navController?.navigateUp()
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
