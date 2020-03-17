package one.cyanpowered.network.protocol

import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap

class ProtocolRegistry<T : Protocol> {
    private val names = ConcurrentHashMap<String, T>()
    private val sockets = ConcurrentHashMap<Int, T>()
    val protocols: Collection<T> = names.values

    fun registerProtocol(port: Int, protocol: T) {
        names[protocol.name] = protocol
        sockets[port] = protocol
    }

    fun getProtocol(name: String): T? = names[name]

    fun getProtocol(address: SocketAddress): T? {
        return if (address is InetSocketAddress) {
            sockets[address.port]
        } else null
    }

    companion object
}