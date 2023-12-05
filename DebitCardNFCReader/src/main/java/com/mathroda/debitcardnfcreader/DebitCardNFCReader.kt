package com.mathroda.debitcardnfcreader

import android.nfc.Tag
import android.nfc.tech.IsoDep
import com.mathroda.debitcardnfcreader.model.DebitCard
import com.mathroda.debitcardnfcreader.utils.DebitCardReader
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

@DelicateCoroutinesApi
internal object DebitCardNFCReader {
    private const val ISO_DEP_TAG = "android.nfc.tech.IsoDep"
    private const val NFC_A_TAG = "android.nfc.tech.NfcA"
    private const val NFC_B_TAG = "android.nfc.tech.NfcB"

    fun readCard(
        tag: Tag?,
        callback: (CardReaderResult) -> Unit
    ) {
        if (tag == null) {
            callback(CardReaderResult.UnsupportedCard)
            return
        }

        val tagString = tag.toString()
        if (!tagString.contains(ISO_DEP_TAG) ||
            (!tagString.contains(NFC_A_TAG) && !tagString.contains(NFC_B_TAG))
        ) {
            callback(CardReaderResult.UnsupportedCard)
           return
        }

        val isoDep = IsoDep.get(tag)
        if (isoDep == null) {
            callback(CardReaderResult.CardMovedToFastOrNFCLocked)
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            readTheCard(isoDep, callback)
        }
    }

    private fun readTheCard(
        isoDep: IsoDep,
        callback: (CardReaderResult) -> Unit
    ) {
        try {
            isoDep.connect()
            val parser = DebitCardReader(isoDep)
            val card = parser.readDebitCard()

            if (card.cardNumber.isNotEmpty()) {
                callback(CardReaderResult.CardSuccessfullyCaptured(card))
            } else {
                callback(CardReaderResult.CardMovedToFastOrNFCLocked)
            }
        } catch (e: IOException) {
            callback(CardReaderResult.UnsupportedCard)
        } finally {
            try {
                isoDep.close()
            } catch (ignored: IOException) {}
         }
    }
}

sealed interface CardReaderResult {
    data class CardSuccessfullyCaptured(val card: DebitCard): CardReaderResult
    object CardMovedToFastOrNFCLocked: CardReaderResult
    object UnsupportedCard: CardReaderResult
}