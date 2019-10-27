package com.viictrp.financeapp.service

import android.content.Context
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.LancamentoRepository
import com.viictrp.financeapp.transformation.LancamentoAssembler
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewObject.LancamentoVO
import java.util.*

class LancamentoService(context: Context) {

    private val repository = LancamentoRepository(context)
    private val cartaoDomain = CartaoService(context)
    private val carteiraDomain = CarteiraService(context)

    fun calcularValorTotal(lancamentos: List<LancamentoVO>): Double {
        if (lancamentos.isEmpty()) return Constantes.ZERO.toDouble()
        return lancamentos.map { lancamento -> lancamento.valor!! }
            .reduce { soma, next -> soma + next }
    }

    fun removerLancamentoPorId(lancamentoId: Long) {
        repository.deleteById(lancamentoId)
    }

    fun buscarLancamentosDaFatura(faturaId: Long): List<LancamentoVO> {
        val lancamentos = repository.findLancamentosByFaturaId(faturaId)
        return LancamentoAssembler.instance.toViewObject(lancamentos)
    }

    fun buscarLancamentosDaCarteira(carteiraId: Long, mes: Int, ano: Int): List<LancamentoVO> {
        val lancamentos = repository.findLancamentosByCarteiraId(carteiraId, mes, ano)
        return LancamentoAssembler.instance.toViewObject(lancamentos)
    }

    fun salvarNoCartao(lancamento: Lancamento, cartaoId: Long) {
        if (lancamento.quantidadeParcelas > Constantes.UM) {
            val lancamentos = clonar(lancamento, lancamento.quantidadeParcelas)
            val valorTotal =
                calcularValorTotal(LancamentoAssembler.instance.toViewObject(lancamentos))
            cartaoDomain.somarAoLimiteDisponivelDoCartao(cartaoId, valorTotal)
            lancamentos.forEach {
                salvarNaFatura(it, cartaoId)
            }
        } else {
            salvarNaFatura(lancamento, cartaoId)
            cartaoDomain.somarAoLimiteDisponivelDoCartao(cartaoId, lancamento.valor!!)
        }
    }

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

    fun salvarNaCarteira(lancamento: Lancamento, carteiraId: Long) {
        if (carteiraId != Constantes.ZERO_LONG) {
            val carteira = carteiraDomain.buscarCarteiraPorId(carteiraId)
            lancamento.carteiraId = carteira.id
            repository.save(lancamento)
        }
    }

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