package one.cyanpowered.network.protocol

import one.cyanpowered.network.Packet
import one.cyanpowered.network.PacketHandler
import one.cyanpowered.network.session.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class AbstractProtocol
@JvmOverloads
constructor(
        override val name: String,
        val logger: Logger = LoggerFactory.getLogger("Protocol.$name")
) : Protocol {
    abstract fun <S : Session, M : Packet> getPacketHandler(message: Class<M>): PacketHandler<S, M>?

    companion object
}

fun <S : Session, M : Packet> AbstractProtocol.getPacketHandler(message: KClass<M>): PacketHandler<S, M>? = getPacketHandler(message.java)
inline fun <S : Session, reified M : Packet> AbstractProtocol.getPacketHandler() = getPacketHandler<S, M>(M::class)