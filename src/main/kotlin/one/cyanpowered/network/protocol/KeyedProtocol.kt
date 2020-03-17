package one.cyanpowered.network.protocol

import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Message
import one.cyanpowered.network.service.CodecLookupService
import one.cyanpowered.network.service.HandlerLookupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

abstract class KeyedProtocol(
        name: String,
        val maxMessages: Int = 0,
        logger: Logger = LoggerFactory.getLogger("Protocol.$name")
) : AbstractProtocol(name, logger) {
    private val codecLookup = ConcurrentHashMap<String, CodecLookupService>()
    private val handlerLookup = ConcurrentHashMap<String, HandlerLookupService>()

    fun getCodecLookupService(key: String) = codecLookup[key]
    fun getHandlerLookupService(key: String) = handlerLookup[key]

    @JvmOverloads
    open fun <M : Message> registerMessage(
            key: String, message: Class<M>, codec: Codec<M>, opcode: Int? = null
    ): CodecRegistration? {
        return try {
            val codecLookup = codecLookup.getOrPut(key) {
                CodecLookupService(maxMessages)
            }
            codecLookup.bind(message, codec, opcode)
        } catch (e: Exception) {
            logger.error("Error registering codec $codec: ", e)
            null
        }
    }
}

fun <M : Message> KeyedProtocol.registerMessage(
        key: String, message: KClass<M>, codec: Codec<M>, opcode: Int? = null
): CodecRegistration? = registerMessage(key, message.java, codec, opcode)

inline fun <reified M : Message> KeyedProtocol.registerMessage(
        key: String, codec: Codec<M>, opcode: Int? = null
): CodecRegistration? = registerMessage(key, M::class, codec, opcode)