package one.cyanpowered.network.pipeline

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import one.cyanpowered.network.ConnectionManager
import one.cyanpowered.network.Message
import one.cyanpowered.network.session.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

open class ChannelMessageHandler(
        private val connectionManager: ConnectionManager
) : SimpleChannelInboundHandler<Message>() {
    protected val logger: Logger
        get() = session?.logger ?: LoggerFactory.getLogger(javaClass.simpleName)
    private val _session = AtomicReference<Session?>(null)
    val session: Session?
        get() = _session.get()

    override fun channelActive(ctx: ChannelHandlerContext) {
        val c = ctx.channel()
        val s = connectionManager.newSession(c)
        check(_session.compareAndSet(null, s)) { "Session may not be set more than once" }
        s.onReady()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val session = session!!
        session.onDisconnect()
        connectionManager.sessionInactivated(session)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, i: Message) {
        session?.messageReceived(i)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        session?.onInboundThrowable(cause)
    }
}