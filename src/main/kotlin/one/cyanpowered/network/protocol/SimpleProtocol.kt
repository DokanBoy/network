package one.cyanpowered.network.protocol

import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Message
import one.cyanpowered.network.MessageHandler
import one.cyanpowered.network.service.CodecLookupService
import one.cyanpowered.network.service.HandlerLookupService
import one.cyanpowered.network.session.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class SimpleProtocol(
        name: String,
        maxMessages: Int = 0,
        logger: Logger = LoggerFactory.getLogger("Protocol.$name")
) : AbstractProtocol(name, logger) {
    val codecLookup: CodecLookupService = CodecLookupService(maxMessages)
    val handlerLookup: HandlerLookupService = HandlerLookupService()

    override fun <M : Message> getCodecRegistration(message: Class<M>): CodecRegistration = codecLookup.find(message)

    override fun <S : Session, M : Message> getMessageHandler(message: Class<M>): MessageHandler<S, M>? = handlerLookup.find(message)

    @JvmOverloads
    open fun <M : Message> registerMessage(
            message: Class<M>, codec: Codec<M>, opcode: Int? = null
    ): CodecRegistration? {
        return try {
            codecLookup.bind(message, codec, opcode)
        } catch (e: Exception) {
            logger.error("Error registering codec $codec: ", e)
            null
        }
    }
}

fun <M : Message> SimpleProtocol.registerMessage(
        message: KClass<M>, codec: Codec<M>, opcode: Int? = null
): CodecRegistration? = registerMessage(message.java, codec,opcode)

inline fun <reified M : Message> SimpleProtocol.registerMessage(codec: Codec<M>, opcode: Int? = null): CodecRegistration? =
        registerMessage(M::class.java,codec, opcode)