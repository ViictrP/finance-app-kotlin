package com.viictrp.financeapp.transformation

import com.viictrp.financeapp.model.Carteira
import com.viictrp.financeapp.viewModel.CarteiraVO

class CarteiraAssembler : Assembler<Carteira, CarteiraVO>() {

    companion object {
        private var mInstance: CarteiraAssembler? = null
        val instance: CarteiraAssembler
            get() {
                if (mInstance == null) {
                    mInstance = CarteiraAssembler()
                }
                return mInstance!!
            }
    }

    override fun toEntity(r: CarteiraVO): Carteira {
        return Carteira().apply {
            this.usuarioId = r.usuarioId
            this.mes = r.mes
            this.ano = r.ano
            this.id = r.id
        }
    }

    override fun toViewObject(e: Carteira): CarteiraVO {
        return CarteiraVO().apply {
            this.ano = e.ano
            this.mes = e.mes
            this.usuarioId = e.usuarioId
            this.id = e.id
        }
    }
}