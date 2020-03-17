package one.cyanpowered.network.protocol

import one.cyanpowered.network.Message
import one.cyanpowered.network.MessageHandler
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
    abstract fun <S : Session, M : Message> getMessageHandler(message: Class<M>): MessageHandler<S, M>?

    companion object
}

fun <S : Session, M : Message> AbstractProtocol.getMessageHandler(message: KClass<M>): MessageHandler<S, M>? = getMessageHandler(message.java)
inline fun <S : Session, reified M : Message> AbstractProtocol.getMessageHandler() = getMessageHandler<S, M>(M::class)