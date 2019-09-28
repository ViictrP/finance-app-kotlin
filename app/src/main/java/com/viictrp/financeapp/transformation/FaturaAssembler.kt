package com.viictrp.financeapp.transformation

import com.viictrp.financeapp.model.Fatura
import com.viictrp.financeapp.viewModel.FaturaVO

class FaturaAssembler : Assembler<Fatura, FaturaVO>() {

    companion object {
        private var mInstance: FaturaAssembler? = null
        val instance: FaturaAssembler
            get() {
                if (mInstance == null) {
                    mInstance = FaturaAssembler()
                }
                return mInstance!!
            }
    }

    override fun toEntity(r: FaturaVO): Fatura {
        return Fatura().apply {
            this.titulo = r.titulo
            this.pago = r.pago
            this.usuarioId = r.usuarioId
            this.descricao = r.descricao
            this.diaFechamento = r.diaFechamento
            this.ano = r.ano
            this.mes = r.mes
            this.cartaoId = r.cartaoId
            this.id = r.id
        }
    }

    override fun toViewObject(e: Fatura): FaturaVO {
        return FaturaVO().apply {
            this.titulo = e.titulo
            this.pago = e.pago
            this.usuarioId = e.usuarioId
            this.descricao = e.descricao
            this.diaFechamento = e.diaFechamento
            this.ano = e.ano
            this.mes = e.mes
            this.cartaoId = e.cartaoId
            this.id = e.id
        }
    }
}