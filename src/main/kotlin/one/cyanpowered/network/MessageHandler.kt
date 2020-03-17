package one.cyanpowered.network

import one.cyanpowered.network.session.Session

interface MessageHandler<S : Session, M : Message> {
    fun handle(session: S, message: M)

    companion object
}