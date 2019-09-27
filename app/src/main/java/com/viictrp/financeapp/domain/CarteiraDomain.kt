package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.repository.CarteiraRepository
import com.viictrp.financeapp.repository.OrcamentoRepository
import com.viictrp.financeapp.transformation.CarteiraAssembler
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewModel.CarteiraVO
import com.viictrp.financeapp.viewModel.OrcamentoVO

class CarteiraDomain(context: Context) {

    private val repository = CarteiraRepository(context)
    private val orcamentoRepository = OrcamentoRepository(context)

    fun save(carteira: Carteira): CarteiraVO {
        repository.save(carteira)
        return CarteiraAssembler.instance.toViewObject(carteira)
    }

    fun buscarPorMesEAno(mes: Int, ano: Int): CarteiraVO? {
        val month = CustomCalendarView.getMonthDescription(mes)
        val carteira = this.repository.findCarteiraByMes(month!!, ano)
        if (carteira == null) {
            carteira = criarNovaCarteira(month, ano)
        }
        return CarteiraAssembler.instance.toViewObject(carteira!!)
    }

    /**
     * Cria uma nova carteira caso não exista carteiras para o mês atual
     */
    private fun criarNovaCarteira(mes: String, ano: Int) {
        val carteira = Carteira().apply {
            this.usuarioId = 1
            this.mes = mes
            this.ano = ano
        }
        repository.save(carteira)
        val orcamento = criarNovoOrcamento(carteira.id, carteira.mes, carteira.ano)
        val carteiraVO = CarteiraAssembler.instance.toViewObject(carteira)
        carteiraVO.orcamento = orcamento
    }

    private fun criarNovoOrcamento(carteiraId: Long?, mes: String?, ano: Int?) {
        val orcamento = Orcamento().apply {
            this.carteiraId = carteiraId
            this.mes = mes
            this.ano = ano
            this.valor = Constantes.ZERO.toDouble()
        }
        orcamentoRepository.save(orcamento) {
            finish(OrcamentoVO())
        }
    }
}