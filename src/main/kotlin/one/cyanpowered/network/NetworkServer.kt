package one.cyanpowered.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.SocketAddress

abstract class NetworkServer : ConnectionManager {
    private val boosGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()
            .group(boosGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(BaseChannelInitializer(this))
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)

    fun bind(address: SocketAddress) = bootstrap.bind(address).addListener {
        if (it.isSuccess) {
            onBindSuccess(address)
        } else {
            onBindFailure(address, it.cause())
        }
    }

    open fun onBindSuccess(address: SocketAddress) {}

    open fun onBindFailure(address: SocketAddress, t: Throwable) {
        t.printStackTrace()
    }

    override fun shutdown() {
        workerGroup.shutdownGracefully()
        boosGroup.shutdownGracefully()
    }
}