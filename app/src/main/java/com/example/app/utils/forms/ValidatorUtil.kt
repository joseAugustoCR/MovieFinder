package aioria.com.br.kotlinbaseapp.utils.forms

import android.text.TextUtils


object FormValidatorUtil {
    private val pesoCPF = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    private val pesoCNPJ = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

    fun isPasswordValid(password: String): Boolean {
        return password.length > 7
    }

    fun isEmailValid(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


    fun isValidCPF(cpf: String?): Boolean {
        if (cpf == null || cpf.length != 11) return false

        if (cpf == "11111111111" || cpf == "22222222222"
                || cpf == "33333333333" || cpf == "44444444444"
                || cpf == "55555555555" || cpf == "66666666666"
                || cpf == "77777777777" || cpf == "88888888888"
                || cpf == "99999999999" || cpf.length != 11) {
            return false
        }

        val digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF)
        val digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF)
        return cpf == cpf.substring(0, 9) + digito1.toString() + digito2.toString()
    }

    fun isValidCNPJ(cnpj: String?): Boolean {
        if (cnpj == null || cnpj.length != 14) return false

        val digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ)
        val digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ)
        return cnpj == cnpj.substring(0, 12) + digito1.toString() + digito2.toString()
    }

    private fun calcularDigito(str: String, peso: IntArray): Int {
        var soma = 0
        var indice = str.length - 1
        var digito: Int
        while (indice >= 0) {
            digito = Integer.parseInt(str.substring(indice, indice + 1))
            soma += digito * peso[peso.size - str.length + indice]
            indice--
        }
        soma = 11 - soma % 11
        return if (soma > 9) 0 else soma
    }
}
