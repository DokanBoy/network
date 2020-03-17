package one.cyanpowered.network

interface Packet {
    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    companion object
}