package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.repository.CartaoRepository
import com.viictrp.financeapp.repository.FaturaRepository
import com.viictrp.financeapp.transformation.FaturaAssembler
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewObject.FaturaVO

class CartaoDomain(context: Context) {

    private val cartaoRepository = CartaoRepository(context)
    private val faturaRepository = FaturaRepository(context)

    /**
     * Busca os cartões do usuário logado
     * @param usuarioId Código do usuário
     * @return lista de cartões
     */
    fun buscarCartaoPorUsuario(usuarioId: Long): List<Cartao> {
        return cartaoRepository.findCartaoByUsuarioId(usuarioId)
    }

    /**
     * Salva um novo cartão no banco de dados
     * @param cartao novo cartão
     * @param finish função que será executada ao término da operação
     */
    fun salvarCartao(cartao: Cartao, finish: () -> Unit) {
        cartaoRepository.save(cartao) {
            finish()
        }
    }

    /**
     * Busca a fatura do cartão informado referente ao mês e ano informados.
     * Se não houver fatura para o período, uma nova fatura é criada e retornada
     * @param cartaoId código do cartão
     * @param mes mês de referência
     * @param ano ano de referência
     * @return fatura encontrada (ou criada)
     */
    fun buscarFaturaPorCartaoMesEAno(cartaoId: Long, mes: String, ano: Int): FaturaVO? {
        var fatura = faturaRepository.findByCartaoIdAndMesAndAno(cartaoId, mes, ano)
        if (fatura == null) {
            fatura = criarFatura(cartaoId, mes, ano)
        }
        return FaturaAssembler.instance.toViewObject(fatura)
    }

    /**
     * Busca uma fatura pelo seu código de identificação
     * @param faturaId código da fatura
     * @return fatura encontrada
     */
    private fun buscarFaturaPorId(faturaId: Long): FaturaVO? {
        return FaturaAssembler.instance.toViewObject(faturaRepository.findById(faturaId)!!)
    }

    /**
     * Busca a fatura do cartão para o mês e ano fornecidos. Executa a lógica
     * de fechamento da fatura, para quando o dia fornecido for maior que o dia
     * do fechamento da fatura, será devolvido a fatura do próximo mês.
     * @param cartaoId - Código do cartão
     * @param mes - mês da fatura
     * @param dia - dia do lançamento
     * @param ano - ano da fatura
     * @return {FaturaVO}
     */
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

    /**
     * Cria uma nova fatura para o cartão, no mês e ano informados.
     * @param cartaoId código do cartão
     * @param mes mês da fatura
     * @param ano ano da fatura
     * @return fatura criada
     */
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
}
