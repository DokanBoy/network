package one.cyanpowered.network.protocol

import io.netty.buffer.ByteBuf
import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Message
import one.cyanpowered.network.exception.UnknownMessageException
import kotlin.reflect.KClass

interface Protocol {
    val name: String

    @Throws(UnknownMessageException::class)
    fun readHeader(byteBuf: ByteBuf): Codec<*>
    fun writeHeader(header: ByteBuf, codecRegistration: CodecRegistration, data: ByteBuf): ByteBuf

    fun <T : Message> getCodecRegistration(message: Class<T>): CodecRegistration
    fun <T : Message> getCodecRegistration(message: KClass<T>) = getCodecRegistration(message.java)

    companion object
}