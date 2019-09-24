package com.viictrp.financeapp.domain

import android.content.Context
import com.viictrp.financeapp.model.Cartao
import com.viictrp.financeapp.repository.CartaoRepository

class CartaoDomain(private var context: Context) {

    private val cartaoRepository = CartaoRepository(context)

    fun findCartaoByUsuarioId(usuarioId: Long): List<Cartao> {
        return cartaoRepository.findCartaoByUsuarioId(usuarioId)
    }

    fun save(cartao: Cartao, finish: () -> Unit) {
        cartaoRepository.save(cartao) {
            finish()
        }
    }
}
