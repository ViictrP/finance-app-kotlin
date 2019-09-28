package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.LancamentoRepository
import com.viictrp.financeapp.utils.Constantes

class LancamentoDomain(context: Context) {

    private val repository = LancamentoRepository(context)

    /**
     * Soma todos os valores dos lançamentos dentro da lista recebida
     * @param lancamentos Lista de lançamentos
     */
    fun calcularValorTotal(lancamentos: List<Lancamento>): Double {
        if (lancamentos.isEmpty()) return Constantes.ZERO.toDouble()
        return lancamentos.map { lancamento -> lancamento.valor!! }
            .reduce { soma, next -> soma + next }
    }

    /**
     * Apaga um lançamento do banco de dados
     * @param lancamento Lançamento que será apagado
     */
    fun removerLancamento(lancamento: Lancamento) {
        repository.delete(lancamento)
    }

    /**
     * Busca os lançamentos da fatura
     * @param faturaId Código da fatura
     */
    fun buscarLancamentosDaFatura(faturaId: Long): List<Lancamento> {
        return repository.findLancamentosByFaturaId(faturaId)
    }

    /**
     * Busca os lançamentos da carteira para o período do mês e ano informados
     * @param carteiraId código da carteira
     * @param mes mês do lançamento
     * @param ano ano do lançamento
     * @return lista de lançamentos
     */
    fun buscarLancamentosDaCarteira(carteiraId: Long, mes: Int, ano: Int): List<Lancamento> {
        return repository.findLancamentosByCarteiraId(carteiraId, mes, ano)
    }
}