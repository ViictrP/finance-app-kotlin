package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.CartaoRepository
import com.viictrp.financeapp.ui.custom.CustomCalendarView

class CartaoDomain(context: Context) {

    private val cartaoRepository = CartaoRepository(context)
    private val faturaDomain = FaturaDomain(context)
    private val lancamentoDomain = LancamentoDomain(context)

    /**
     * Busca os cartões do usuário logado
     * @param usuarioId Código do usuário
     * @return lista de cartões
     */
    fun buscarPorUsuario(usuarioId: Long): List<Cartao> {
        return cartaoRepository.findCartaoByUsuarioId(usuarioId)
    }

    /**
     * Salva um novo cartão no banco de dados
     * @param cartao novo cartão
     * @param finish função que será executada ao término da operação
     */
    fun salvar(cartao: Cartao, finish: () -> Unit) {
        cartaoRepository.save(cartao) {
            finish()
        }
    }

    /**
     * Busca os lançamentos do cartão. Primeiro busca a fatura do cartão
     * referente ao mês e ano informados e então busca todos os lançamentos
     * dessa fatura
     * @param cartaoId código do cartão
     * @param mes mês de referência
     * @param ano ano de referência
     * @return lista de lançamentos
     */
    fun buscarLancamentosPorMesEAno(cartaoId: Long, mes: Int, ano: Int): List<Lancamento> {
        val fatura = faturaDomain.buscarPorCartaoMesEAno(
            cartaoId,
            CustomCalendarView.getMonthDescription(mes)!!,
            ano
        )
        return lancamentoDomain.buscarLancamentosDaFatura(fatura!!.id!!)
    }
}
