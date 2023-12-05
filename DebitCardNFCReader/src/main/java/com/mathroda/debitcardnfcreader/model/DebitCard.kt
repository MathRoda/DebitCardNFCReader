package com.mathroda.debitcardnfcreader.model

import com.mathroda.debitcardnfcreader.model.commandhelper.DebitCardTags
import com.mathroda.debitcardnfcreader.utils.ByteUtils
import com.mathroda.debitcardnfcreader.utils.TypeLengthValueHandler
import java.util.regex.Pattern

data class DebitCard(
    var holderName: String = "",
    var cardNumber: String = "",
    var expireDateMonth: String = "",
    var expireDateYear: String = "",
    var secondCardNumber: String = "",
    var secondExpireDateMonth: String = "",
    var secondExpireDateYear: String = "",
    var emvCardService: DebitCardService? = null,
    var isNfcLocked: Boolean = true
) {
    /**
     * Extract track 2 data
     * @return true if the extraction succeed
     */
    internal fun setTrack2Data(data: ByteArray?): Boolean {
        val track2 = TypeLengthValueHandler.getValue(data, DebitCardTags.TRACK_2_EQV_DATA, DebitCardTags.TRACK2_DATA)

        if (track2 != null) {
            val stringData = ByteUtils.bytesToStringNoSpace(track2)
            val matches = TRACK2_PATTERN.matcher(stringData)

            // Check pattern, read card number, date and service
            if (matches.find()) {
                if(cardNumber.isEmpty()) {
                    cardNumber = matches.group(1)?: ""
                    expireDateMonth = matches.group(2)?.substring(2, 4)?: ""
                    expireDateYear = matches.group(2)?.substring(0, 2)?: ""
                    emvCardService = DebitCardService(matches.group(3)?: "")
                } else if(cardNumber != matches.group(1)) {
                    secondCardNumber = matches.group(1)?: ""
                    secondExpireDateMonth = matches.group(2)?.substring(2, 4)?: ""
                    secondExpireDateYear = matches.group(2)?.substring(0, 2)?: ""
                }
                return true
            }
        }
        return false
    }

    companion object{
        private val TRACK2_PATTERN = Pattern.compile("([0-9]{1,19})D([0-9]{4})([0-9]{3})?(.*)")
    }
}
