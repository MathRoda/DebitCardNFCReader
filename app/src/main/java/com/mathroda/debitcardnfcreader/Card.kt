package com.mathroda.debitcardnfcreader

import androidx.annotation.DrawableRes

enum class Card(
    @DrawableRes val logo: Int
) {
    None(R.drawable.ic_visa),
    Visa(R.drawable.ic_visa),
    Mastercard(R.drawable.ic_mastercard),
    AmericanExpress(R.drawable.amex_logo),
}