package one.cyanpowered.network.session

import one.cyanpowered.network.Packet
import one.cyanpowered.network.exception.ChannelClosedException
import one.cyanpowered.network.processor.PacketProcessor
import one.cyanpowered.network.protocol.Protocol
import org.slf4j.Logger

interface Session {
    fun <T : Packet> packetReceived(message: T)

    val logger: Logger
    val protocol: Protocol
    val processor: PacketProcessor? get() = null

    @Throws(ChannelClosedException::class)
    fun send(packet: Packet)

    @Throws(ChannelClosedException::class)
    fun sendAll(vararg packets: Packet)

    fun disconnect()
    fun onDisconnect()
    fun onReady()

    fun onInboundThrowable(throwable: Throwable)

    companion object
}