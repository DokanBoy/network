@file:JvmName("ByteBufUtils")

package one.cyanpowered.network.util

import io.netty.buffer.ByteBuf
import java.io.IOException
import java.nio.charset.StandardCharsets

fun ByteBuf.readUTF8(): String {
    val len: Int = readVarInt()
    val bytes = ByteArray(len)
    readBytes(bytes)
    return String(bytes, StandardCharsets.UTF_8)
}

fun ByteBuf.writeUTF8(value: String) {
    val bytes = value.toByteArray(StandardCharsets.UTF_8)
    if (bytes.size >= Short.MAX_VALUE) {
        throw IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuf!")
    }
    writeVarInt(bytes.size)
    writeBytes(bytes)
}

fun ByteBuf.readVarInt(): Int {
    var output = 0
    var size = 0
    var input: Int
    while (true) {
        input = readByte().toInt()
        if (input and 0x80 != 0x80) break
        output = output or (input and 0x7F shl size++ * 7)
        if (size > 5) {
            throw IOException("Attempt to read int bigger than allowed for a varint!")
        }
    }
    return output or (input and 0x7F shl size * 7)
}

fun ByteBuf.writeVarInt(value: Int) {
    var v = value
    var part: Int
    while (true) {
        part = (v and 0x7F)
        v = v ushr 7
        if (value != 0) {
            part = part or 0x80
        }
        writeByte(part)
        if (v == 0) {
            break
        }
    }
}

fun ByteBuf.readVarLong(): Long {
    var output: Long = 0
    var bytes = 0
    var input: Int
    while (true) {
        input = readByte().toInt()
        output = output or (input and 0x7F shl bytes++ * 7).toLong()
        if (bytes > 10) {
            throw IOException("Attempt to read long bigger than allowed for a varlong!")
        }
        if (input and 0x80 != 0x80) {
            break
        }
    }
    return output
}

fun writeVarLong(buf: ByteBuf, value: Long) {
    var v = value
    var part: Long
    while (true) {
        part = v and 0x7F
        v = v ushr 7
        if (v != 0L) {
            part = part or 0x80
        }
        buf.writeByte(part.toInt())
        if (v == 0L) {
            break
        }
    }
}
