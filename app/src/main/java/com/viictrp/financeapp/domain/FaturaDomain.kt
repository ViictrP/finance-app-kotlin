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

    /**
     * Busca a fatura do cartão informado referente ao mês e ano informados.
     * Se não houver fatura para o período, uma nova fatura é criada e retornada
     * @param cartaoId código do cartão
     * @param mes mês de referência
     * @param ano ano de referência
     * @return fatura encontrada (ou criada)
     */
    fun buscarPorCartaoMesEAno(cartaoId: Long, mes: String, ano: Int): Fatura? {
        var fatura = repository.findByCartaoIdAndMesAndAno(cartaoId, mes, ano)
        if (fatura == null) {
            fatura = criar(cartaoId, mes, ano)
        }
        return fatura
    }

    /**
     * Busca uma fatura pelo seu código de identificação
     * @param faturaId código da fatura
     * @return fatura encontrada
     */
    fun buscarPorId(faturaId: Long): Fatura? {
        return repository.findById(faturaId)
    }

    /**
     * Cria uma nova fatura para o cartão, no mês e ano informados.
     * @param cartaoId código do cartão
     * @param mes mês da fatura
     * @param ano ano da fatura
     * @return fatura criada
     */
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