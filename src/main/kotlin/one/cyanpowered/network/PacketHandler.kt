package one.cyanpowered.network

import one.cyanpowered.network.session.Session

interface PacketHandler<S : Session, P : Packet> {
    fun handle(session: S, packet: P)

    companion object
}