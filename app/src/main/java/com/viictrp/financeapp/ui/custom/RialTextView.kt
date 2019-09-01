package com.viictrp.financeapp.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class RialTextView : TextView {

    private var rawText: String = ""

    constructor(context: Context) : super(context) { }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { }

    override fun setText(text: CharSequence, type: BufferType) {
        rawText = text.toString()
        var prezzo = text.toString()
        try {

            val symbols = DecimalFormatSymbols()
            symbols.decimalSeparator = ','
            symbols.groupingSeparator = '.'
            val decimalFormat = DecimalFormat("#,###", symbols)
            decimalFormat.maximumFractionDigits = 2
            prezzo = decimalFormat.format(text.toString().toDouble())
        } catch (e: Exception) {
        }

        super.setText("R$${prezzo}", type)
    }

    override fun getText(): CharSequence {

        return rawText
    }
}