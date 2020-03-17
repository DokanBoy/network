package one.cyanpowered.network.session

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import one.cyanpowered.network.Packet
import one.cyanpowered.network.PacketHandler
import one.cyanpowered.network.exception.ChannelClosedException
import one.cyanpowered.network.protocol.AbstractProtocol
import org.slf4j.Logger
import java.util.*

open class BaseSession(
        val channel: Channel,
        override val protocol: AbstractProtocol
) : Session {
    val uniqueId = UUID.randomUUID()
    override val logger: Logger
        get() = protocol.logger
    val isActive: Boolean
        get() = channel.isActive

    private fun handlePacket(packet: Packet) {
        val messageClass = packet.javaClass
        val handler = protocol.getPacketHandler<Session,Packet>(messageClass)
        if (handler != null) {
            try {
                handler.handle(this, packet)
            } catch (t: Throwable) {
                onHandlerThrowable(packet, handler, t)
            }
        }
    }

    override fun <T : Packet> packetReceived(message: T) {
        handlePacket(message)
    }

    @Throws(ChannelClosedException::class)
    fun sendWithFuture(packet: Packet): ChannelFuture {
        if (!channel.isActive) {
            throw ChannelClosedException("Trying to send a message when a session is inactive!")
        }
        return channel.writeAndFlush(packet).addListener {
            if (it.cause() != null) {
                onOutboundThrowable(it.cause())
            }
        }
    }

    override fun send(packet: Packet) {
        sendWithFuture(packet)
    }

    override fun sendAll(vararg packets: Packet) {
        for (message in packets) {
            send(message)
        }
    }

    override fun disconnect() {
        channel.close()
    }

    override fun onDisconnect() {
    }

    override fun onReady() {
    }

    override fun onInboundThrowable(throwable: Throwable) {}

    fun onOutboundThrowable(throwable: Throwable?) {}


    fun onHandlerThrowable(packet: Packet, handler: PacketHandler<*, *>, throwable: Throwable) {}
}