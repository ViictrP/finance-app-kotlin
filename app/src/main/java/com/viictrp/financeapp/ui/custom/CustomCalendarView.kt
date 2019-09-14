package com.viictrp.financeapp.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.viictrp.financeapp.R
import java.util.*

class CustomCalendarView : LinearLayout, View.OnClickListener {

    private lateinit var onMonthChangeListener: OnMonthChangeListener

    companion object {

        private val months = mapOf(
            1 to "JANEIRO",
            2 to "FEVEREIRO",
            3 to "MARÇO",
            4 to "ABRIL",
            5 to "MAIO",
            6 to "JUNHO",
            7 to "JULHO",
            8 to "AGOSTO",
            9 to "SETEMBRO",
            10 to "OUTUBRO",
            11 to "NOVEMBRO",
            12 to "DEZEMBRO"
        )

        val JANEIRO = 1
        val FEVEREIRO = 2
        val MARCO = 3
        val ABRIL = 4
        val MAIO = 5
        val JUNHO = 6
        val JULHO = 7
        val AGOSTO = 8
        val SETEMBRO = 9
        val OUTUBRO = 10
        val NOVEMBRO = 11
        val DEZEMBRO = 12

        /**
         * Obtém a descrição de um mês
         *
         * @param month - posição do mês no map
         */
        fun getMonthDescription(month: Int): String? {
            return when (month) {
                JANEIRO -> return months[JANEIRO]
                FEVEREIRO -> return months[FEVEREIRO]
                MARCO -> return months[MARCO]
                ABRIL -> return months[ABRIL]
                MAIO -> return months[MAIO]
                JUNHO -> return months[JUNHO]
                JULHO -> return months[JULHO]
                AGOSTO -> return months[AGOSTO]
                SETEMBRO -> return months[SETEMBRO]
                OUTUBRO -> return months[OUTUBRO]
                NOVEMBRO -> return months[NOVEMBRO]
                DEZEMBRO -> return months[DEZEMBRO]
                else -> null
            }
        }

        fun getNextMonth(month: Int): String? {
            return if (month == DEZEMBRO) {
                getMonthDescription(JANEIRO)
            } else {
                getMonthDescription(month + 1)
            }
        }

        fun getNextMonth(month: String): String? {
            val monthId = CustomCalendarView.getMonthId(month)
            return if (monthId == DEZEMBRO) {
                getMonthDescription(JANEIRO)
            } else {
                getMonthDescription(monthId!! + 1)
            }
        }

        fun getMonthId(month: String): Int? {
            return when (month) {
                "JANEIRO" -> return JANEIRO
                "FEVEREIRO" -> return FEVEREIRO
                "MARCO" -> return MARCO
                "ABRIL" -> return ABRIL
                "MAIO" -> return MAIO
                "JUNHO" -> return JUNHO
                "JULHO" -> return JULHO
                "AGOSTO" -> return AGOSTO
                "SETEMBRO" -> return SETEMBRO
                "OUTUBRO" -> return OUTUBRO
                "NOVEMBRO" -> return NOVEMBRO
                "DEZEMBRO" -> return DEZEMBRO
                else -> null
            }
        }
    }


    private var month: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var root: View = LayoutInflater.from(context)
        .inflate(R.layout.custom_calendar_layout, this, true)

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        orientation = VERTICAL
        root.findViewById<ImageButton>(R.id.previous_month).setOnClickListener(this)
        root.findViewById<ImageButton>(R.id.next_month).setOnClickListener(this)
        root.findViewById<TextView>(R.id.custom_calendar_tx_month).text = getMonthDescription(month)
    }

    /**
     * seta o listener
     */
    fun setOnMonthChangeListener(listener: OnMonthChangeListener) {
        this.onMonthChangeListener = listener
    }

    /**
     * Seta o mês
     */
    fun setMonth(month: Int) {
        val textView = root.findViewById<TextView>(R.id.custom_calendar_tx_month)
        textView.text = months[month]
        this.month = month
    }

    /**
     * OnClick dos botões
     */
    override fun onClick(button: View?) {
        button.let {
            when (it!!.id) {
                R.id.previous_month -> onPreviousMonthChange()
                R.id.next_month -> onNextMonthChange()
            }
        }
    }

    /**
     * Executado quando o botão anterior for clicado
     */
    private fun onPreviousMonthChange() {
        if (month == JANEIRO) {
            this.monthChanged(DEZEMBRO)
        } else {
            val previous = month - 1
            this.monthChanged(previous)
        }
    }

    /**
     * Executado quando o botão próximo for executado
     */
    private fun onNextMonthChange() {
        if (month == DEZEMBRO) {
            this.monthChanged(JANEIRO)
        } else {
            val next = month + 1
            this.monthChanged(next)
        }
    }

    /**
     * Quando um mês é alterado
     *
     * @param month - Mês da vez
     */
    private fun monthChanged(month: Int) {
        val textView = root.findViewById<TextView>(R.id.custom_calendar_tx_month)
        textView.text = months[month]
        this.month = month
        this.onMonthChangeListener.onMonthChange(month)
    }

    interface OnMonthChangeListener {

        /**
         * Chamado toda vez que um dos botões forem clicados
         *
         * @param month - mês selecionado
         */
        fun onMonthChange(month: Int)
    }
}