package net.illuc.kontraption.peripherals

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import net.illuc.kontraption.blockEntities.TileEntityConnector

class ConnectorPeripheral(
    private val blockEntity: TileEntityConnector,
) : IDynamicPeripheral {
    private val attachedComputers = mutableSetOf<IComputerAccess>()

    override fun getType(): String = "Connector"

    override fun getMethodNames(): Array<String> =
        arrayOf(
            "connect",
            "disconnect",
            "isConnected",
            "getConnectionID",
            "enableCC",
            "disableCC",
        )

    private companion object {
        const val CONNECT = 0
        const val DISCONNECT = 1
        const val IS_CONNECTED = 2
        const val GET_ID = 3
        const val ENABLE_CC = 4
        const val DISABLE_CC = 5
    }
    // Using magicc numbers WAS annoying

    override fun callMethod(
        computer: IComputerAccess?,
        context: ILuaContext?,
        method: Int,
        arguments: IArguments?,
    ): MethodResult {
        return when (method) {
            CONNECT -> {
                if (blockEntity.underCC) {
                    return if (!blockEntity.isConnected) {
                        blockEntity.connect()
                        MethodResult.of("Trying to connect")
                    } else {
                        MethodResult.of("Already connected")
                    }
                }
                MethodResult.of("Connector is not under CC")
            }

            DISCONNECT -> {
                if (blockEntity.underCC) {
                    return if (blockEntity.isConnected) {
                        blockEntity.disconnect()
                        MethodResult.of("Disconnected")
                    } else {
                        MethodResult.of("Not connected to anything")
                    }
                }
                MethodResult.of("Connector is not under CC")
            }

            IS_CONNECTED -> MethodResult.of(blockEntity.isConnected)

            GET_ID -> MethodResult.of(blockEntity.conid)

            ENABLE_CC -> {
                blockEntity.underCC = true
                MethodResult.of(true)
            }

            DISABLE_CC -> {
                blockEntity.underCC = false
                MethodResult.of(true)
            }

            else -> MethodResult.of(null)
        }
    }

    fun emitEvent(
        event: String,
        vararg args: Any?,
    ) {
        attachedComputers.forEach { it.queueEvent(event, *args) }
    }

    override fun attach(computer: IComputerAccess) {
        attachedComputers.add(computer)
    }

    override fun detach(computer: IComputerAccess) {
        attachedComputers.remove(computer)
    }

    override fun equals(other: IPeripheral?): Boolean = other is ConnectorPeripheral && other.blockEntity == this.blockEntity

    override fun hashCode(): Int = blockEntity.hashCode()
}
