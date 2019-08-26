package com.viictrp.financeapp.ui.custom

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import faranjit.currency.edittext.R

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.Currency
import java.util.Locale

/**
 * Created by bulent.turkmen on 8/9/2016.
 */
class CurrencyEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {
    private var mGroupDivider: Char = ' '
    private var mMonetaryDivider: Char = ' '
    private var mLocale: String? = ""
    private var mShowSymbol: Boolean = false

    private var groupDivider: Char = ' '
    private var monetaryDivider: Char = ' '

    private var locale: Locale? = null
    private var numberFormat: DecimalFormat? = null

    private var fractionDigit: Int = 0
    private var currencySymbol: String? = null

    private val defaultLocale: Locale
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.resources.configuration.locales.get(0)
        else
            context.resources.configuration.locale

    private val onTextChangeListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length == 0)
                return

            removeTextChangedListener(this)

            /***
             * Clear input to get clean text before format
             * '\u0020' is empty character
             */
            var text = s.toString()
            text = text.replace(groupDivider, '\u0020').replace(monetaryDivider, '\u0020')
                .replace(".", "").replace(" ", "")
                .replace(currencySymbol!!, "").trim { it <= ' ' }
            try {
                text = format(text)
            } catch (e: ParseException) {
                Log.e(javaClass.canonicalName, e.message)
            }

            setText(text)
            setSelection(text.length)

            addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    /**
     *
     * @return double value for current text
     */
    val currencyDouble: Double
        @Throws(ParseException::class)
        get() {
            var text = text.toString()
            text = text.replace(groupDivider, '\u0020')
                .replace(monetaryDivider, '\u0020')
                .replace(".", "")
                .replace(" ", "")
                .replace(currencySymbol!!, "")
                .replace("\\s".toRegex(), "")
                .trim { it <= ' ' }

            return if (showSymbol())
                java.lang.Double.parseDouble(text.replace(currencySymbol!!, "")) / Math.pow(
                    10.0,
                    fractionDigit.toDouble()
                )
            else
                java.lang.Double.parseDouble(text) / Math.pow(10.0, fractionDigit.toDouble())
        }

    /**
     *
     * @return String value for current text
     */
    val currencyText: String
        @Throws(ParseException::class)
        get() = if (showSymbol())
            text.toString().replace(currencySymbol!!, "")
        else
            text.toString()

    init {
        this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.currencyEditText, 0, 0)

        try {
            if (a.getString(R.styleable.currencyEditText_groupDivider) != null) {
                this.mGroupDivider = a.getString(R.styleable.currencyEditText_groupDivider)!![0]
                this.groupDivider = mGroupDivider
            }

            if (a.getString(R.styleable.currencyEditText_monetaryDivider) != null) {
                this.mMonetaryDivider =
                    a.getString(R.styleable.currencyEditText_monetaryDivider)!![0]
                this.monetaryDivider = mMonetaryDivider
            }

            if (a.getString(R.styleable.currencyEditText_locale) == null)
                this.locale = defaultLocale
            else
                this.mLocale = a.getString(R.styleable.currencyEditText_locale)

            if (a.getString(R.styleable.currencyEditText_showSymbol) != null)
                this.mShowSymbol = a.getBoolean(R.styleable.currencyEditText_showSymbol, false)

            if (mLocale == "") {
                locale = defaultLocale
            } else {
                if (mLocale!!.contains("-"))
                    mLocale = mLocale!!.replace("-", "_")

                val l = mLocale!!.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (l.size > 1) {
                    locale = Locale(l[0], l[1])
                } else {
                    locale = Locale("", mLocale!!)
                }
            }

            initSettings()
        } finally {
            a.recycle()
        }

        this.addTextChangedListener(onTextChangeListener)
    }

    /***
     * If user does not provide a valid locale it throws IllegalArgumentException.
     *
     * If throws an IllegalArgumentException the locale sets to default locale
     */
    private fun initSettings() {
        var success = false
        while (!success) {
            try {
                fractionDigit = Currency.getInstance(locale).defaultFractionDigits

                val symbols = DecimalFormatSymbols.getInstance(locale)
                if (mGroupDivider.toInt() > 0)
                    symbols.groupingSeparator = mGroupDivider
                groupDivider = symbols.groupingSeparator

                if (mMonetaryDivider.toInt() > 0)
                    symbols.monetaryDecimalSeparator = mMonetaryDivider
                monetaryDivider = symbols.monetaryDecimalSeparator

                currencySymbol = symbols.currencySymbol

                val df = DecimalFormat.getCurrencyInstance(locale!!) as DecimalFormat
                numberFormat = DecimalFormat(df.toPattern(), symbols)

                success = true
            } catch (e: IllegalArgumentException) {
                Log.e(javaClass.canonicalName, e.message)
                locale = defaultLocale
            }

        }
    }

    /***
     * It resets text currently displayed If user changes separators or locale etc.
     */
    private fun resetText() {
        var s = text.toString()
        if (s.isEmpty()) {
            initSettings()
            return
        }

        s = s.replace(groupDivider, '\u0020').replace(monetaryDivider, '\u0020')
            .replace(".", "").replace(" ", "")
            .replace(currencySymbol!!, "").trim { it <= ' ' }
        try {
            initSettings()
            s = format(s)
            removeTextChangedListener(onTextChangeListener)
            setText(s)
            setSelection(s.length)
            addTextChangedListener(onTextChangeListener)
        } catch (e: ParseException) {
            Log.e(javaClass.canonicalName, e.message)
        }

    }

    @Throws(ParseException::class)
    private fun format(text: String): String {
        val formattedText = text.replace("\\s".toRegex(), "")
        return if (mShowSymbol)
            numberFormat!!.format(
                java.lang.Double.parseDouble(formattedText) / Math.pow(
                    10.0,
                    fractionDigit.toDouble()
                )
            )
        else
            numberFormat!!.format(
                java.lang.Double.parseDouble(formattedText) / Math.pow(
                    10.0,
                    fractionDigit.toDouble()
                )
            ).replace(currencySymbol!!, "")
    }

    /***
     * returns the decimal separator for current locale
     * for example; input value 1,234.56
     * returns ','
     *
     * @return decimal separator char
     */
    fun getGroupDivider(): Char {
        return groupDivider
    }

    /***
     * sets how to divide decimal value and fractions
     * for example; If you want formatting like this
     * for input value 1,234.56
     * set ','
     * @param groupDivider char
     */
    fun setGroupDivider(groupDivider: Char) {
        this.mGroupDivider = groupDivider
        resetText()
    }

    /***
     * returns the monetary separator for current locale
     * for example; input value 1,234.56
     * returns '.'
     *
     * @return monetary separator char
     */
    fun getMonetaryDivider(): Char {
        return monetaryDivider
    }

    /***
     * sets how to divide decimal value and fractions
     * for example; If you want formatting like this
     * for input value 1,234.56
     * set '.'
     * @param monetaryDivider char
     */
    fun setMonetaryDivider(monetaryDivider: Char) {
        this.mMonetaryDivider = monetaryDivider
        resetText()
    }

    /***
     *
     * @return current locale
     */
    fun getLocale(): Locale? {
        return locale
    }

    /***
     * Sets locale which desired currency format
     *
     * @param locale
     */
    fun setLocale(locale: Locale) {
        this.locale = locale
        resetText()
    }

    /**
     *
     * @return true if currency symbol of current locale is showing
     */
    fun showSymbol(): Boolean {
        return this.mShowSymbol
    }

    /***
     * Sets if currency symbol of current locale shows
     *
     * @param showSymbol
     */
    fun showSymbol(showSymbol: Boolean) {
        this.mShowSymbol = showSymbol
        resetText()
    }
}