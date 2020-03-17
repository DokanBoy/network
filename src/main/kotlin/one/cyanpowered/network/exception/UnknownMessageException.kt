package one.cyanpowered.network.exception

class UnknownMessageException(
        message: String,
        val opcode: Int,
        val length: Int
) : Exception(message)