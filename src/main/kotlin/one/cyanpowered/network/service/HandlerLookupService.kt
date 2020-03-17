package one.cyanpowered.network.service

import one.cyanpowered.network.Message
import one.cyanpowered.network.MessageHandler
import one.cyanpowered.network.session.Session
import java.util.*

class HandlerLookupService {
    private val handlers: MutableMap<Class<out Message>, MessageHandler<*, *>> = HashMap()

    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun <M : Message, H : MessageHandler<*, in M>> bind(clazz: Class<M>, handlerClass: Class<H>) {
        val handler: MessageHandler<*, in M> = handlerClass.getConstructor().newInstance()
        handlers[clazz] = handler
    }

    @Suppress("UNCHECKED_CAST")
    fun <S: Session, M : Message> find(clazz: Class<M>): MessageHandler<S, M>? = handlers[clazz] as MessageHandler<S, M>

    override fun toString(): String = "HandlerLookupService{handlers=$handlers}"
}