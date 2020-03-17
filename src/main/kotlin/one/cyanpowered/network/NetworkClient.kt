package one.cyanpowered.network

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.SocketAddress

abstract class NetworkClient : ConnectionManager {
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
            .group(workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .handler(BaseChannelInitializer(this))

    fun bind(address: SocketAddress) = bootstrap.connect(address).addListener {
        if (it.isSuccess) {
            onBindSuccess(address)
        } else {
            onBindFailure(address, it.cause())
        }
    }

    fun <T> preConnectOption(option: ChannelOption<T>, value: T) = bootstrap.option(option, value)

    open fun onBindSuccess(address: SocketAddress) {}

    open fun onBindFailure(address: SocketAddress, t: Throwable) {}

    override fun shutdown() {
        workerGroup.shutdownGracefully()
    }
}