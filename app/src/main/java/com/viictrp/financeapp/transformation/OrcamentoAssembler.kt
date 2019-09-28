package com.viictrp.financeapp.transformation

import com.viictrp.financeapp.model.Orcamento
import com.viictrp.financeapp.viewModel.OrcamentoVO

class OrcamentoAssembler : Assembler<Orcamento, OrcamentoVO>() {

    companion object {
        private var mInstance: OrcamentoAssembler? = null
        val instance: OrcamentoAssembler
            get() {
                if (mInstance == null) {
                    mInstance = OrcamentoAssembler()
                }
                return mInstance!!
            }
    }

    override fun toEntity(r: OrcamentoVO): Orcamento {
        return Orcamento().apply {
            this.valor = r.valor
            this.id = r.id
            this.carteiraId = r.carteiraId
            this.ano = r.ano
            this.mes = r.mes
        }
    }

    override fun toViewObject(e: Orcamento): OrcamentoVO {
        return OrcamentoVO().apply {
            this.id = e.id
            this.carteiraId = e.carteiraId
            this.valor = e.valor
            this.ano = e.ano
            this.mes = e.mes
        }
    }
}