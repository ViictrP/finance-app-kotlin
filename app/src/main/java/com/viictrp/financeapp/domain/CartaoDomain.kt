package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.repository.CartaoRepository
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import java.util.*

class CartaoDomain(private var context: Context) {

    private val cartaoRepository = CartaoRepository(context)
    private val faturaDomain = FaturaDomain(context)

    fun findCartaoByUsuarioId(usuarioId: Long): List<Cartao> {
        return cartaoRepository.findCartaoByUsuarioId(usuarioId)
    }

    fun save(cartao: Cartao, finish: () -> Unit) {
        cartaoRepository.save(cartao) {
            finish()
        }
    }

    fun findLancamentos(cartaoId: Long, mes: Int, ano: Int): List<Lancamento> {
        val fatura = faturaDomain.findByCartaoIdAndMesAndAno(
            cartaoId,
            CustomCalendarView.getMonthDescription(mes)!!,
            ano
        )
        return faturaDomain.findLancamentosByFaturaId(fatura!!.id!!)
    }
}
