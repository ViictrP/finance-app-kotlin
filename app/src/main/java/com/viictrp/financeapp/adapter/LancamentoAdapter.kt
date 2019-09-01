package com.viictrp.financeapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.viictrp.financeapp.R
import com.viictrp.financeapp.model.Lancamento
import kotlinx.android.synthetic.main.lancamento_recyclerview_item.view.*

class LancamentoAdapter(private var lancamentos: List<Lancamento>?,
                        private val context: Context) : Adapter<LancamentoAdapter.LancamentoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LancamentoViewHolder {
        val view = LayoutInflater.from(context)
                                 .inflate(R.layout.lancamento_recyclerview_item, parent, false)
        return LancamentoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lancamentos!!.size
    }

    fun setList(lancamentos: List<Lancamento>) {
        this.lancamentos = lancamentos
    }

    override fun onBindViewHolder(holder: LancamentoViewHolder, position: Int) {
        holder.bindView(lancamentos!![position])
    }

    class LancamentoViewHolder(itemView: View) : ViewHolder(itemView) {

        val titulo = itemView.lancamento_titulo
        val descricao = itemView.lancamento_descricao
        val data = itemView.lancamento_data
        val icon = itemView.lancamento_icon
        val valor = itemView.lancamento_valor

        fun bindView(lancamento: Lancamento) {
            titulo.text = lancamento.titulo
            descricao.text = lancamento.descricao
            data.text = lancamento.data
            valor.text = "R$${lancamento.valor}"
        }
    }

}
