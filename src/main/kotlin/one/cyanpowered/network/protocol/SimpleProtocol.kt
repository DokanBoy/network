package one.cyanpowered.network.protocol

import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Packet
import one.cyanpowered.network.PacketHandler
import one.cyanpowered.network.service.CodecLookupService
import one.cyanpowered.network.service.HandlerLookupService
import one.cyanpowered.network.session.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class SimpleProtocol(
        name: String,
        maxPackets: Int = 0,
        logger: Logger = LoggerFactory.getLogger("Protocol.$name")
) : AbstractProtocol(name, logger) {
    val codecLookup: CodecLookupService = CodecLookupService(maxPackets)
    val handlerLookup: HandlerLookupService = HandlerLookupService()

    override fun <T : Packet> getCodecRegistration(message: Class<T>): CodecRegistration = codecLookup.find(message)

    override fun <S : Session, M : Packet> getPacketHandler(message: Class<M>): PacketHandler<S, M>? = handlerLookup.find(message)

    @JvmOverloads
    open fun <T : Packet> registerPacket(
            message: Class<T>, codec: Codec<T>, opcode: Int? = null
    ): CodecRegistration? {
        return try {
            codecLookup.bind(message, codec, opcode)
        } catch (e: Exception) {
            logger.error("Error registering codec $codec: ", e)
            null
        }
    }
}

fun <T : Packet> SimpleProtocol.registerPacket(
        message: KClass<T>, codec: Codec<T>, opcode: Int? = null
): CodecRegistration? = registerPacket(message.java, codec,opcode)

inline fun <reified T : Packet> SimpleProtocol.registerPacket(codec: Codec<T>, opcode: Int? = null): CodecRegistration? =
        registerPacket(T::class.java,codec, opcode)