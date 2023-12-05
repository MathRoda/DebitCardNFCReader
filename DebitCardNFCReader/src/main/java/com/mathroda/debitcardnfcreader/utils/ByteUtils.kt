package com.mathroda.debitcardnfcreader.utils

import java.util.Locale
import kotlin.math.roundToInt

internal object ByteUtils {

    fun byteArrayToInt(byteArray: ByteArray): Int {
        return if (byteArray.size <= 4) {
            var value = 0
            for (i in byteArray.indices) {
                value += (byteArray[i].toInt() and 255) shl byteArray.size - i - 1
            }
            value
        } else {
            throw IllegalArgumentException("Parameter \'byteArray\' size cannot be greater than 4")
        }
    }

    fun bytesToString(byteArray: ByteArray?): String {
        return formatByte(byteArray, "%02x ")
    }

    fun bytesToStringNoSpace(byteArray: ByteArray?): String {
        return formatByte(byteArray, "%02x")
    }

    /**
     * @return string representation of ByteArray or empty string if array is null
     */
    private fun formatByte(byteArray: ByteArray?, format: String): String {
        val stringBuilder = StringBuilder()
        if (byteArray == null) {
            stringBuilder.append("")
        } else {
            var notFirst = false
            for (byte in byteArray) {
                if (byte.toInt() != 0 || notFirst) {
                    notFirst = true
                    stringBuilder.append(String.format(format, Integer.valueOf(byte.toInt() and 255)))
                }
            }
        }
        return stringBuilder.toString().uppercase(Locale.getDefault()).trim { it <= ' ' }
    }

    /**
     * Opposite of formatByte - @return ByteArray from String representation
     */
    fun byteArrayFromString(stringToArray: String): ByteArray {
        val text = stringToArray.replace(" ", "")
        require(text.length % 2 == 0)
        val commandByte = ByteArray((text.length.toFloat() / 2.0f).roundToInt())
        var j = 0
        var i = 0
        while (i < text.length) {
            commandByte[j++] = Integer.valueOf(text.substring(i, i + 2).toInt(16)).toByte()
            i += 2
        }
        return commandByte
    }

    /**
     * Checking specific bit in int - is it on
     * @return true if bit == 1
     * for example value 7 (00000111) and bitIndex 2 will return true
     */
    fun matchBitByBitIndex(value: Int, bitIndex: Int): Boolean {
        return if (bitIndex in 0..31) {
            (value and (1 shl bitIndex)) != 0
        } else {
            false
        }
    }

    /**
     * Switching on/off specific bit in byte
     * @bitIndex must be between 0 <= bitIndex && bitIndex <= 7
     * (one byte have 8 bits)
     */
    fun setBit(data: Byte, bitIndex: Int, isOn: Boolean): Byte {
        return if (isOn) {
            (data.toInt() or (1 shl bitIndex)).toByte()
        } else {
            (data.toInt() and (1 shl bitIndex).inv()).toByte()
        }
    }

}