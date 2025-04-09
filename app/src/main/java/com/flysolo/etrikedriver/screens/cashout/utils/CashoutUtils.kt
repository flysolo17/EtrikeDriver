package com.flysolo.etrikedriver.screens.cashout.utils

import com.flysolo.etrikedriver.screens.cashout.RecipientType
import com.flysolo.etrikedriver.ui.theme.TextFieldData

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

 fun isValidPhone(phone: String): Boolean {
    val phoneRegex = Regex("^\\+639\\d{9}\$")
    return phoneRegex.matches(phone)
}

 fun isValidPayPalID(paypalID: String): Boolean {
    return paypalID.isNotEmpty() && paypalID.length in 6..64
}


 fun validateAccount(account: String, recipientType: RecipientType): TextFieldData {
    return when (recipientType) {
        RecipientType.EMAIL -> {
            if (isValidEmail(account)) {
                TextFieldData(value = account, hasError = false, errorMessage = null)
            } else {
                TextFieldData(value = account, hasError = true, errorMessage = "Invalid email format")
            }
        }

        RecipientType.PHONE -> {
            if (isValidPhone(account)) {
                TextFieldData(value = account, hasError = false, errorMessage = null)
            } else {
                TextFieldData(value = account, hasError = true, errorMessage = "Invalid phone format. Use +639XXXXXXXXX")
            }
        }

        RecipientType.PAYPAL_ID -> {
            if (isValidPayPalID(account)) {
                TextFieldData(value = account, hasError = false, errorMessage = null)
            } else {
                TextFieldData(value = account, hasError = true, errorMessage = "Invalid PayPal ID")
            }
        }
    }
}