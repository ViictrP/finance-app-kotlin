package com.viictrp.financeapp.transformation

import com.viictrp.financeapp.model.Lancamento
import com.viictrp.financeapp.viewObject.LancamentoVO

class LancamentoAssembler : Assembler<Lancamento, LancamentoVO>() {

    companion object {
        private var mInstance: LancamentoAssembler? = null
        val instance: LancamentoAssembler
            get() {
                if (mInstance == null) {
                    mInstance = LancamentoAssembler()
                }
                return mInstance!!
            }
    }

    override fun toEntity(r: LancamentoVO): Lancamento {
        return Lancamento().apply {
            this.id = r.id
            this.carteiraId = r.carteiraId
            this.categoriaId = r.categoriaId
            this.data = r.data
            this.descricao = r.descricao
            this.faturaId = r.faturaId
            if (r.numeroParcela != null) {
                this.numeroParcela = r.numeroParcela!!
            }
            this.parcelaId = r.parcelaId
            this.titulo = r.titulo
            this.valor = r.valor
            if (r.quantidadeParcelas != null) {
                this.quantidadeParcelas = r.quantidadeParcelas!!
            }
        }
    }

    override fun toViewObject(e: Lancamento): LancamentoVO {
        return LancamentoVO().apply {
            this.id = e.id
            this.carteiraId = e.carteiraId
            this.categoriaId = e.categoriaId
            this.data = e.data
            this.descricao = e.descricao
            this.faturaId = e.faturaId
            this.numeroParcela = e.numeroParcela
            this.parcelaId = e.parcelaId
            this.titulo = e.titulo
            this.valor = e.valor
            this.quantidadeParcelas = e.quantidadeParcelas
        }
    }
}