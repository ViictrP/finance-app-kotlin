package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.CartaoRepository
import com.viictrp.financeapp.repository.FaturaRepository
import com.viictrp.financeapp.repository.LancamentoRepository

class FaturaDomain(context: Context) {

    private val repository = FaturaRepository(context)
    private val cartaoRepository = CartaoRepository(context)
    private val lancamentoRepository = LancamentoRepository(context)

    fun findByCartaoIdAndMesAndAno(cartaoId: Long, mes: String, ano: Int): Fatura? {
        var fatura = repository.findByCartaoIdAndMesAndAno(cartaoId, mes, ano)
        if (fatura == null) {
            fatura = criar(cartaoId, mes, ano)
        }
        return fatura
    }

    fun findById(faturaId: Long): Fatura? {
        return repository.findById(faturaId)
    }

    fun findLancamentosByFaturaId(faturaId: Long): List<Lancamento> {
        return lancamentoRepository.findLancamentosByFaturaId(faturaId)
    }

    private fun criar(cartaoId: Long, mes: String, ano: Int): Fatura {
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
        repository.save(fatura)
        return fatura
    }
}