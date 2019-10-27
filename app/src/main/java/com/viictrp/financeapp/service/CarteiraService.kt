package com.viictrp.financeapp.service

import android.content.Context
import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.repository.CarteiraRepository
import com.viictrp.financeapp.repository.OrcamentoRepository
import com.viictrp.financeapp.transformation.CarteiraAssembler
import com.viictrp.financeapp.transformation.OrcamentoAssembler
import com.viictrp.financeapp.ui.custom.CustomCalendarView
import com.viictrp.financeapp.utils.Constantes
import com.viictrp.financeapp.viewObject.CarteiraVO
import com.viictrp.financeapp.viewObject.OrcamentoVO

class CarteiraService(context: Context) {

    private val repository = CarteiraRepository(context)
    private val orcamentoRepository = OrcamentoRepository(context)

    private fun save(carteira: Carteira): CarteiraVO {
        repository.save(carteira)
        return CarteiraAssembler.instance.toViewObject(carteira)
    }

    fun buscarCarteiraPorId(carteiraId: Long): CarteiraVO {
        val carteira = repository.findById(carteiraId)
        return CarteiraAssembler.instance.toViewObject(carteira!!)
    }

    fun buscarPorMesEAno(mes: Int, ano: Int): CarteiraVO? {
        val month = CustomCalendarView.getMonthDescription(mes)
        val carteira = this.repository.findCarteiraByMes(month!!, ano)
        return if (carteira == null) criarNovaCarteira(month, ano)
        else {
            val orcamento = orcamentoRepository.findOrcamentoByCarteiraId(carteira.id!!)
            val carteiraVO = CarteiraAssembler.instance.toViewObject(carteira)
            carteiraVO.orcamento = OrcamentoAssembler.instance.toViewObject(orcamento!!)
            return carteiraVO
        }
    }

    private fun criarNovaCarteira(mes: String, ano: Int): CarteiraVO {
        val carteira = Carteira().apply {
            this.usuarioId = 1
            this.mes = mes
            this.ano = ano
        }
        val carteiraVO = this.save(carteira)
        val orcamento = criarNovoOrcamento(carteira.id, carteira.mes, carteira.ano)
        carteiraVO.orcamento = orcamento
        return carteiraVO
    }

    private fun criarNovoOrcamento(carteiraId: Long?, mes: String?, ano: Int?): OrcamentoVO {
        val orcamento = orcamentoRepository.save(Orcamento().apply {
            this.carteiraId = carteiraId
            this.mes = mes
            this.ano = ano
            this.valor = Constantes.ZERO.toDouble()
        })
        return OrcamentoAssembler.instance.toViewObject(orcamento)
    }
}