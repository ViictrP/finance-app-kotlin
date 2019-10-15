package com.viictrp.financeapp.domain

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.LancamentoRepository
import com.viictrp.financeapp.transformation.LancamentoAssembler
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewObject.LancamentoVO
import java.util.*

class LancamentoDomain(context: Context) {

    private val repository = LancamentoRepository(context)
    private val cartaoDomain = CartaoDomain(context)
    private val carteiraDomain = CarteiraDomain(context)

    /**
     * Soma todos os valores dos lançamentos dentro da lista recebida
     * @param lancamentos Lista de lançamentos
     */
    fun calcularValorTotal(lancamentos: List<LancamentoVO>): Double {
        if (lancamentos.isEmpty()) return Constantes.ZERO.toDouble()
        return lancamentos.map { lancamento -> lancamento.valor!! }
            .reduce { soma, next -> soma + next }
    }

    /**
     * Apaga um lançamento do banco de dados
     * @param lancamento Lançamento que será apagado
     */
    fun removerLancamentoPorId(lancamentoId: Long) {
        repository.deleteById(lancamentoId)
    }

    /**
     * Busca os lançamentos da fatura
     * @param faturaId Código da fatura
     */
    fun buscarLancamentosDaFatura(faturaId: Long): List<LancamentoVO> {
        val lancamentos = repository.findLancamentosByFaturaId(faturaId)
        return LancamentoAssembler.instance.toViewObject(lancamentos)
    }

    /**
     * Busca os lançamentos da carteira para o período do mês e ano informados
     * @param carteiraId código da carteira
     * @param mes mês do lançamento
     * @param ano ano do lançamento
     * @return lista de lançamentos
     */
    fun buscarLancamentosDaCarteira(carteiraId: Long, mes: Int, ano: Int): List<LancamentoVO> {
        val lancamentos = repository.findLancamentosByCarteiraId(carteiraId, mes, ano)
        return LancamentoAssembler.instance.toViewObject(lancamentos)
    }

    /**
     * Salva um novo lançamento na fatura do cartão de acordo com a data
     * do lançamento.
     * @param lancamento - novo lançamento
     * @param cartaoId - código do cartão
     */
    fun salvarNoCartao(lancamento: Lancamento, cartaoId: Long) {
        if (lancamento.quantidadeParcelas > Constantes.UM) {
            val lancamentos = clonar(lancamento, lancamento.quantidadeParcelas)
            lancamentos.forEach {
                salvarNaFatura(it, cartaoId)
            }
        } else salvarNaFatura(lancamento, cartaoId)
    }

    /**
     * Salva o lançamento na fatura de acordo com a data de lançamento
     * @param lancamento - novo lançamento
     * @param cartaoId - código do cartão
     */
    private fun salvarNaFatura(lancamento: Lancamento, cartaoId: Long) {
        val calendar = Calendar.getInstance()
        calendar.time = lancamento.data!!
        val fatura =
            cartaoDomain.buscarFatura(
                cartaoId,
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR)
            )
        lancamento.faturaId = fatura!!.id
        repository.save(lancamento)
    }

    /**
     * Salva o lançamento na carteira de acordo com a data do lançamento
     * @param lancamento - novo lançamento
     * @param carteiraId - código da carteira
     */
    fun salvarNaCarteira(lancamento: Lancamento, carteiraId: Long) {
        if (carteiraId != Constantes.ZERO_LONG) {
            val carteira = carteiraDomain.buscarCarteiraPorId(carteiraId)
            lancamento.carteiraId = carteira.id
            repository.save(lancamento)
        }
    }

    /**
     * Clona os lançamentos para a quantidade de parcelas fornecidas e os salva nas
     * respectivas faturas.
     * @param lancamento - lançamento que será clonado
     * @param quantidadeParcelas - quantidade de clones que serão gerados
     * @return {List<Lancamento>}
     */
    private fun clonar(lancamento: Lancamento, quantidadeParcelas: Int): List<Lancamento> {
        val list = mutableListOf(lancamento)
        val calendar = Calendar.getInstance()
        val descricao = lancamento.descricao
        val parcelasId = UUID.randomUUID().toString().substring(Constantes.ZERO, Constantes.DEZ)
        lancamento.descricao = "${lancamento.descricao} ${Constantes.UM}/$quantidadeParcelas"
        lancamento.parcelaId = parcelasId
        lancamento.valor = lancamento.valor!! / quantidadeParcelas
        calendar.time = lancamento.data!!
        for (i in Constantes.UM until quantidadeParcelas) {
            calendar.add(Calendar.MONTH, Constantes.UM)
            list.add(Lancamento().apply {
                this.titulo = lancamento.titulo
                this.descricao =
                    "$descricao ${(i + Constantes.UM)}/$quantidadeParcelas"
                this.data = calendar.time
                this.valor = lancamento.valor
                this.quantidadeParcelas = lancamento.quantidadeParcelas
                this.parcelaId = parcelasId
                this.numeroParcela = i
            })
        }
        return list
    }
}