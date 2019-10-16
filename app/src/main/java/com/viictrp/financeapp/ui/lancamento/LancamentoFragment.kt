package com.viictrp.financeapp.ui.lancamento

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.viictrp.financeapp.MainActivity
import com.viictrp.financeapp.R
import com.viictrp.financeapp.service.LancamentoService
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.ui.custom.CurrencyEditText
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import java.util.*

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

    private lateinit var lancamentoService: LancamentoService

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
        this.lancamentoService = LancamentoService(this.context!!)
        view.findViewById<CardView>(R.id.btn_salvar).setOnClickListener(this)
        viewModel.carteiraId.postValue(arguments?.getLong(Constantes.CARTEIRA_ID_KEY))
        viewModel.cartaoId.postValue(arguments?.getLong(Constantes.CARTAO_ID_KEY))
        bindEditTexts(view)
        bindObservers()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {
        val lancamento = getLancamento()
        this.viewModel.carteiraId.value.let {
            if (it != null && it != Constantes.ZERO_LONG) {
                lancamentoService.salvarNaCarteira(lancamento, it)
            }
        }
        this.viewModel.cartaoId.value.let {
            if (it != null && it != Constantes.ZERO_LONG) {
                lancamentoService.salvarNoCartao(lancamento, it)
            }
        }
        showMessageAndExit(
            "Lançamento do dia ${CustomCalendarView.getFormattedDate(lancamento.data!!)} salvo com sucesso."
        )
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
        lancamento.data = this.calendar.time
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
            this.etData!!.setText(CustomCalendarView.getFormattedDate(this.calendar.time))
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
}
