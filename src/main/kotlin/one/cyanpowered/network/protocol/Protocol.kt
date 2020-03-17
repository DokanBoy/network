package one.cyanpowered.network.protocol

import io.netty.buffer.ByteBuf
import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Packet
import one.cyanpowered.network.exception.UnknownPacketException
import kotlin.reflect.KClass

interface Protocol {
    val name: String

    @Throws(UnknownPacketException::class)
    fun readHeader(byteBuf: ByteBuf): Codec<*>

    fun <T : Packet> getCodecRegistration(message: Class<T>): CodecRegistration
    fun <T : Packet> getCodecRegistration(message: KClass<T>) = getCodecRegistration(message.java)

    fun writeHeader(header: ByteBuf, codecRegistration: CodecRegistration, data: ByteBuf): ByteBuf

    companion object
}