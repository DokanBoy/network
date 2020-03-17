package one.cyanpowered.network.exception

class UnknownPacketException(
        message: String,
        val opcode: Int,
        val length: Int
) : Exception(message)