package aioria.com.br.kotlinbaseapp.utils.forms

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.*


class BRCurrencyTextWatcher(private val editText: EditText) : TextWatcher {

    private var lastAmount = ""

    private var lastCursorPosition = -1

    override fun onTextChanged(amount: CharSequence, start: Int, before: Int, count: Int) {

        if (amount.toString() != lastAmount) {

            val cleanString = clearCurrencyToNumber(amount.toString())

            try {

                val formattedAmount = transformToCurrency(cleanString)
                editText.removeTextChangedListener(this)
                editText.setText(formattedAmount)
                editText.setSelection(formattedAmount.length)
                editText.addTextChangedListener(this)

                if (lastCursorPosition != lastAmount.length && lastCursorPosition != -1) {
                    val lengthDelta = formattedAmount.length - lastAmount.length
                    val newCursorOffset = Math.max(0, Math.min(formattedAmount.length, lastCursorPosition + lengthDelta))
                    editText.setSelection(newCursorOffset)
                }
            } catch (e: Exception) {
                //log something
            }

        }
    }

    override fun afterTextChanged(s: Editable) {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        val value = s.toString()
        if (value != "") {
            val cleanString = clearCurrencyToNumber(value)
            val formattedAmount = transformToCurrency(cleanString)
            lastAmount = formattedAmount
            lastCursorPosition = editText.selectionStart
        }
    }

    companion object {

        fun clearCurrencyToNumber(currencyValue: String?): String {
            var result: String? = null

            if (currencyValue == null) {
                result = ""
            } else {
                result = currencyValue.replace("[(a-z)|(A-Z)|($,. )]".toRegex(), "")
            }
            return result
        }

        fun getFormattedValue(currencyValue: String?): String {
            var result: String? = null

            if (currencyValue == null) {
                result = ""
            } else {
                result = currencyValue.replace("[(a-z)|(A-Z)|($ )]".toRegex(), "")
                result = result.replace(",", ".")
            }
            return result
        }

        fun isCurrencyValue(currencyValue: String?, podeSerZero: Boolean): Boolean {
            val result: Boolean

            if (currencyValue == null || currencyValue.length == 0) {
                result = false
            } else {
                if (!podeSerZero && currencyValue == "0,00") {
                    result = false
                } else {
                    result = true
                }
            }
            return result
        }

        fun transformToDouble(currentValue: String): Double {
            var parsed:Double = 0.toDouble()
            var value:String = ""

            if (currentValue.isNullOrEmpty()) {
                return 0.toDouble()
            } else {
                value = currentValue.replace("[(a-z)|(A-Z)|($,. )]".toRegex(), "")
            }
            try {
                parsed = java.lang.Double.parseDouble(value)
            }catch (e:java.lang.Exception){

            }

            return parsed
        }

        fun transformToCurrency(value: String): String {
            val parsed = java.lang.Double.parseDouble(value)
            var formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed / 100)
            formatted = formatted.replace("[^(0-9)(.,)]".toRegex(), "")
            return "R$ $formatted"
        }
    }
}