package one.cyanpowered.network.protocol

import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Packet
import one.cyanpowered.network.service.CodecLookupService
import one.cyanpowered.network.service.HandlerLookupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

abstract class KeyedProtocol(
        name: String,
        val maxPackets: Int = 0,
        logger: Logger = LoggerFactory.getLogger("Protocol.$name")
) : AbstractProtocol(name, logger) {
    private val codecLookup = ConcurrentHashMap<String, CodecLookupService>()
    private val handlerLookup = ConcurrentHashMap<String, HandlerLookupService>()

    fun getCodecLookupService(key: String) = codecLookup[key]
    fun getHandlerLookupService(key: String) = handlerLookup[key]

    @JvmOverloads
    open fun <P : Packet> registerMessage(
            key: String, packet: Class<P>, codec: Codec<P>, opcode: Int? = null
    ): CodecRegistration? {
        return try {
            val codecLookup = codecLookup.getOrPut(key) {
                CodecLookupService(maxPackets)
            }
            codecLookup.bind(packet, codec, opcode)
        } catch (e: Exception) {
            logger.error("Error registering codec $codec: ", e)
            null
        }
    }
}

fun <P : Packet> KeyedProtocol.registerPacket(
        key: String, message: KClass<P>, codec: Codec<P>, opcode: Int? = null
): CodecRegistration? = registerMessage(key, message.java, codec, opcode)

inline fun <reified P : Packet> KeyedProtocol.registerPacket(
        key: String, codec: Codec<P>, opcode: Int? = null
): CodecRegistration? = registerPacket(key, P::class, codec, opcode)