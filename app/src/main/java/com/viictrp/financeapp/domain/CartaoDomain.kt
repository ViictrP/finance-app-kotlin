package com.viictrp.financeapp.domain

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

class CartaoDomain(context: Context) {

    private val cartaoRepository = CartaoRepository(context)
    private val faturaRepository = FaturaRepository(context)
    private val pagamentoRepository = PagamentoFaturaRepository(context)

    /**
     * Busca os cartões do usuário logado
     * @param usuarioId Código do usuário
     * @return lista de cartões
     */
    fun buscarCartaoPorUsuario(usuarioId: Long): List<Cartao> {
        return cartaoRepository.findCartaoByUsuarioId(usuarioId)
    }

    /**
     * Busca um cartão para o ID fornecido
     * @param cartaoId - ID do cartão
     * @return {Cartao}
     */
    fun buscarCartaoPorId(cartaoId: Long): Cartao? {
        return cartaoRepository.findById(cartaoId)
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
    fun buscarFaturaPorId(faturaId: Long): FaturaVO? {
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

    /**
     * Adiciona um novo pagamento para a fatura. Se o valor não foi total,
     * um novo lançamento com o saldo é gerado na próxima fatura
     * @param fatura - fatura que será paga
     * @param valor - valor pago
     * @throws RealmNotFoundException - caso a fatura não seja encontrada
     */
    @Throws(RealmNotFoundException::class)
    fun pagarFatura(fatura: FaturaVO, valor: Double) {
        val pagamento = gerarNovoPagamento(fatura.id!!, fatura.mes!!, valor)
        fatura.pago = true
        faturaRepository.update(FaturaAssembler.instance.toEntity(fatura))
        pagamentoRepository.save(pagamento)
    }

    /**
     * Cria um novo pagamento dado o valor e o ID da fatura
     * @param faturaId - ID da fatura
     * @param valor - valor do pagamento
     * @return {PagamentoFatura}
     */
    private fun gerarNovoPagamento(faturaId: Long, mes: String, valor: Double): PagamentoFatura {
        return PagamentoFatura().apply {
            this.data = Date()
            this.faturaId = faturaId
            this.valor = valor
            this.mesReferencia = mes
        }
    }

    /**
     * Verifica se hoje é depois do dia de fechamento do cartão
     * @param cartao - cartão
     * @return {boolean} true se hoje é depois do fechamento
     */
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
}

