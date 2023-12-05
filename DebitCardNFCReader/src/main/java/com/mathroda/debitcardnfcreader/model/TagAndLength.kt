package com.mathroda.debitcardnfcreader.model

import com.mathroda.debitcardnfcreader.model.commandhelper.DebitCardTags
import com.mathroda.debitcardnfcreader.utils.ByteUtils

internal data class TagAndLength(
    val tag: DebitCardTag,
    val length: Int
)

internal fun TagAndLength.constructByteValue(): ByteArray {
    val returnValue = ByteArray(this.length)

    //Terminal Transaction Qualifiers, (Tag '9F66')
    var terminalQuailValue: ByteArray? = null
    if(this.tag === DebitCardTags.TERMINAL_TRANSACTION_QUALIFIERS){

        terminalQuailValue = ByteArray(4)

        //Set Contactless EMV Mode Supported
        terminalQuailValue[0] = ByteUtils.setBit(terminalQuailValue[0], 5, true)

        //Set Reader Is Offline Only
        terminalQuailValue[0] = ByteUtils.setBit(terminalQuailValue[0], 3, true)
    }

    terminalQuailValue?.let{System.arraycopy(terminalQuailValue, 0, returnValue, 0, Math.min(terminalQuailValue.size, returnValue.size))}

    return returnValue
}
