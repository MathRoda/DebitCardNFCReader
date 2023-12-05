package com.mathroda.debitcardnfcreader.model

import com.mathroda.debitcardnfcreader.utils.ByteUtils

internal data class DebitCardTag(
    val tagIdString: String,
    val tagValueType: TagValueTypeEnum,
    val name: String = "",
    val description: String = ""
) {

    val tagIdBytes: ByteArray = ByteUtils.byteArrayFromString(tagIdString)
    private val tagClass: TagClass
    private val type: TagType
    val isConstructed: Boolean
        get() = type === TagType.CONSTRUCTED

    init {
        type = if (ByteUtils.matchBitByBitIndex(tagIdBytes[0].toInt(), 5)) {
            TagType.CONSTRUCTED
        } else {
            TagType.PRIMITIVE
        }

        // Bits 8 and 7 of the first byte of the tag field indicate a class.
        // The value 00 indicates a data object of the universal class.
        // The value 01 indicates a data object of the application class.
        // The value 10 indicates a data object of the context-specific class.
        // The value 11 indicates a data object of the private class.
        tagClass = when ((tagIdBytes[0].toInt() ushr 6 and 0x03)) {
            0x01 -> TagClass.APPLICATION
            0x02 -> TagClass.CONTEXT_SPECIFIC
            0x03 -> TagClass.PRIVATE
            else -> TagClass.UNIVERSAL
        }
    }

    enum class TagClass {
        UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE
    }

    enum class TagType {
        /**
         * The value field of a primitive data object contains a data element for financial transaction interchange
         */
        PRIMITIVE,
        /**
         * The value field of a constructed data object contains one or more primitive or constructed data objects. The value field of
         * a constructed data object is called a template.
         */
        CONSTRUCTED
    }

    enum class TagValueTypeEnum {
        BINARY,
        NUMERIC,
        TEXT,
        MIXED,
        DOL,
        TEMPLATE
    }
}

