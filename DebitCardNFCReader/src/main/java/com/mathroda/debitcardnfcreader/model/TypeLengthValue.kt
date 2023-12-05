package com.mathroda.debitcardnfcreader.model

internal data class TypeLengthValue(
    val tag: DebitCardTag,
    val length: Int,
    val rawEncodedLengthBytes: ByteArray,
    val valueBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypeLengthValue

        if (tag != other.tag) return false
        if (length != other.length) return false
        if (!rawEncodedLengthBytes.contentEquals(other.rawEncodedLengthBytes)) return false
        if (!valueBytes.contentEquals(other.valueBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + length
        result = 31 * result + rawEncodedLengthBytes.contentHashCode()
        result = 31 * result + valueBytes.contentHashCode()
        return result
    }
}
