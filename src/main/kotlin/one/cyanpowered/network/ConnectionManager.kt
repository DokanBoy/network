package one.cyanpowered.network

import io.netty.channel.Channel
import one.cyanpowered.network.session.Session

interface ConnectionManager {
    fun newSession(channel: Channel): Session
    fun sessionInactivated(session: Session)
    fun shutdown()
}