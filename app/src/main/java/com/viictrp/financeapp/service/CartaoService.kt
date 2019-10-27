package com.viictrp.financeapp.service

import android.content.Context
import com.viictrp.financeapp.exceptions.RealmNotFoundException
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.model.PagamentoFatura
import com.viictrp.financeapp.repository.CartaoRepository
import com.viictrp.financeapp.repository.FaturaRepository
import com.viictrp.financeapp.repository.PagamentoFaturaRepository
import com.viictrp.financeapp.transformation.FaturaAssembler
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewObject.FaturaVO
import java.util.*

class CartaoService(context: Context) {

    private val cartaoRepository = CartaoRepository(context)
    private val faturaRepository = FaturaRepository(context)
    private val pagamentoRepository = PagamentoFaturaRepository(context)

    fun buscarCartaoPorUsuario(usuarioId: Long): List<Cartao> {
        return cartaoRepository.findCartaoByUsuarioId(usuarioId)
    }

    fun buscarCartaoPorId(cartaoId: Long): Cartao? {
        return cartaoRepository.findById(cartaoId)
    }

    fun salvarCartao(cartao: Cartao, finish: () -> Unit) {
        cartaoRepository.save(cartao) {
            finish()
        }
    }

    fun buscarFaturaPorCartaoMesEAno(cartaoId: Long, mes: String, ano: Int): FaturaVO? {
        var fatura = faturaRepository.findByCartaoIdAndMesAndAno(cartaoId, mes, ano)
        if (fatura == null) {
            fatura = criarFatura(cartaoId, mes, ano)
        }
        return FaturaAssembler.instance.toViewObject(fatura)
    }

    fun buscarFaturaPorId(faturaId: Long): FaturaVO? {
        return FaturaAssembler.instance.toViewObject(faturaRepository.findById(faturaId)!!)
    }

    fun buscarFatura(cartaoId: Long, mes: Int, dia: Int, ano: Int): FaturaVO? {
        val month = CustomCalendarView.getMonthDescription(mes)!!
        val fatura = buscarFaturaPorCartaoMesEAno(cartaoId, month, ano)
        return if (fatura!!.diaFechamento!! >= dia) fatura else {
            val proximoMes = CustomCalendarView.getNextMonth(mes)
            var anoFatura = ano
            if (CustomCalendarView.getMonthId(proximoMes!!) == CustomCalendarView.JANEIRO) {
                anoFatura += Constantes.UM
            }
            return buscarFaturaPorCartaoMesEAno(fatura.cartaoId!!, proximoMes, anoFatura)
        }
    }

    private fun criarFatura(cartaoId: Long, mes: String, ano: Int): Fatura {
        val cartao = cartaoRepository.findById(cartaoId)
        val fatura = Fatura().apply {
            this.cartaoId = cartao!!.id
            this.descricao = "Fatura do mês de $mes de $ano"
            this.diaFechamento = cartao.dataFechamento
            this.mes = mes
            this.ano = ano
            this.pago = false
            this.titulo = "Fatura do mês de $mes de $ano"
            this.usuarioId = cartao.usuarioId
        }
        faturaRepository.save(fatura)
        return fatura
    }

    @Throws(RealmNotFoundException::class)
    fun pagarFatura(fatura: FaturaVO, valor: Double) {
        val pagamento = gerarNovoPagamento(fatura, valor)
        fatura.pago = true
        faturaRepository.update(FaturaAssembler.instance.toEntity(fatura))
        pagamentoRepository.save(pagamento)
        val cartao = cartaoRepository.findById(fatura.cartaoId!!)
        cartao?.let {
            it.limiteDisponivel = it.limiteDisponivel!! + valor
            cartaoRepository.update(it)
        }
    }

    private fun gerarNovoPagamento(fatura: FaturaVO, valor: Double): PagamentoFatura {
        return PagamentoFatura().apply {
            this.data = Date()
            this.faturaId = fatura.id!!
            this.valor = valor
            this.mesReferencia = fatura.mes!!
            this.anoReferencia = fatura.ano!!
        }
    }

    fun cartaoEstaFechado(cartao: Cartao, mes: Int, ano: Int): Boolean {
        val calendar = Calendar.getInstance()
        val hoje = calendar.get(Calendar.DAY_OF_MONTH)
        val mesCalendar = calendar.get(Calendar.MONTH) + Constantes.UM
        val anoCalendar = calendar.get(Calendar.YEAR)
        if (mes < mesCalendar) return true
        if (ano < anoCalendar) return true
        return hoje >= cartao.dataFechamento!! && mes == mesCalendar && ano == anoCalendar
    }

    fun buscarPagamentosFaturaPorMesAndAno(faturaId: Long, mesId: Int, ano: Int): List<PagamentoFatura> {
        val mes = CustomCalendarView.getMonthDescription(mesId)
        return pagamentoRepository.findByFaturaIdAndMesAndAno(faturaId, mes!!, ano)
    }

    private fun obterPagamentosPorFaturaId(faturaId: Long): List<PagamentoFatura>? {
        return pagamentoRepository.findByFaturaId(faturaId)
    }

    @Throws(RealmNotFoundException::class)
    fun somarAoLimiteDisponivelDoCartao(cartaoId: Long, valor: Double) {
        val cartao = cartaoRepository.findById(cartaoId)
        cartao?.let {
            it.limiteDisponivel = it.limiteDisponivel!! - valor
            cartaoRepository.update(it)
        }
    }
}

