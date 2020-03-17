@file:JvmMultifileClass

package one.cyanpowered.network

import io.netty.buffer.ByteBuf
import java.io.IOException

interface Codec<T : Message> {
    @Throws(IOException::class)
    fun decode(byteBuf: ByteBuf): T

    @Throws(IOException::class)
    fun encode(byteBuf: ByteBuf, message: T): ByteBuf

    companion object
}

data class CodecRegistration(
        val opcode: Int,
        val rawCodec: Codec<*>
) {
    @Suppress("UNCHECKED_CAST")
    fun <M : Message> getCodec(): Codec<M> = rawCodec as Codec<M>
}