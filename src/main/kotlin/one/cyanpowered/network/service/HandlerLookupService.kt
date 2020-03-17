package one.cyanpowered.network.service

import one.cyanpowered.network.Packet
import one.cyanpowered.network.PacketHandler
import one.cyanpowered.network.session.Session
import java.util.*

class HandlerLookupService {
    private val handlers: MutableMap<Class<out Packet>, PacketHandler<*, *>> = HashMap()

    @Throws(InstantiationException::class, IllegalAccessException::class)
    fun <M : Packet, H : PacketHandler<*, in M>> bind(clazz: Class<M>, handlerClass: Class<H>) {
        val handler: PacketHandler<*, in M> = handlerClass.getConstructor().newInstance()
        handlers[clazz] = handler
    }

    @Suppress("UNCHECKED_CAST")
    fun <S: Session, M : Packet> find(clazz: Class<M>): PacketHandler<S, M>? = handlers[clazz] as PacketHandler<S, M>

    override fun toString(): String = "HandlerLookupService{handlers=$handlers}"
}