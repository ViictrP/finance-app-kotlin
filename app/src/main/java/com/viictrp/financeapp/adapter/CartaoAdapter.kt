package com.viictrp.financeapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Cartao
import kotlinx.android.synthetic.main.cartao_recycleview_item.view.*

class CartaoAdapter(
    private var cartoes: MutableList<Cartao>,
    private var context: Context
) : RecyclerView.Adapter<CartaoAdapter.CartaoViewHolder>() {

    private var transitionNameIndex = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaoViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.cartao_recycleview_item, parent, false)
        return CartaoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartoes.size
    }

    override fun onBindViewHolder(holder: CartaoViewHolder, position: Int) {
        holder.bindView(cartoes[position], transitionNameIndex)
        this.transitionNameIndex++
    }

    fun setList(cartoes: MutableList<Cartao>) {
        this.cartoes = cartoes
        this.notifyDataSetChanged()
    }

    class CartaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txValorLimit = itemView.tx_valor_limite
        private val txCartaoBandeira = itemView.tx_cartao_bandeira
        private val txFechamento = itemView.tx_fechamento
        private val txNumeroCartao = itemView.tx_numero_cartao

        fun bindView(cartao: Cartao, transitionNameIndex: Int) {
            itemView.transitionName = "cv_cartao_credito$transitionNameIndex"
            txValorLimit.text = cartao.limite.toString()
            txCartaoBandeira.text = cartao.bandeira
            txFechamento.text = cartao.dataFechamento.toString()
            txNumeroCartao.text = cartao.numeroCartao
        }
    }
}