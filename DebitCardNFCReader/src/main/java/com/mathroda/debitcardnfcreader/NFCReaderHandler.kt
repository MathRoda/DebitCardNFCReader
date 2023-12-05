package com.mathroda.debitcardnfcreader

import android.nfc.NfcAdapter
import android.nfc.Tag
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class NFCReaderHandler(
    val resultCallback: (CardReaderResult) -> Unit
): NfcAdapter.ReaderCallback {

    override fun onTagDiscovered(tag: Tag?) {
        DebitCardNFCReader.readCard(tag) { result ->
            resultCallback(result)
        }
    }

}