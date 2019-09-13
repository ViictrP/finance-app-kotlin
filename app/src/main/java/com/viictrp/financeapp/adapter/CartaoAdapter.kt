package com.viictrp.financeapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Cartao

class CartaoAdapter(
    private var cartoes: MutableList<Cartao>,
    private var context: Context
) : RecyclerView.Adapter<CartaoAdapter.CartaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaoViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.cartao_recycleview_item, parent, false)
        return CartaoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartoes.size
    }

    override fun onBindViewHolder(holder: CartaoViewHolder, position: Int) {
        holder.bindView(cartoes[position])
    }

    fun setList(cartoes: MutableList<Cartao>) {
        this.cartoes = cartoes
        this.notifyDataSetChanged()
    }

    class CartaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(cartao: Cartao) {

        }
    }
}