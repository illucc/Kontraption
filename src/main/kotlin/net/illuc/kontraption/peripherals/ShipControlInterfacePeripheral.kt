package net.illuc.kontraption.peripherals
import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaContext
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IDynamicPeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import net.illuc.kontraption.blockEntities.TileEntityShipControlInterface

class ShipControlInterfacePeripheral(
    private val blockEntity: TileEntityShipControlInterface,
) : IDynamicPeripheral {
    companion object {
        private const val GET_ROTATION = 0
        private const val GET_MOVEMENT = 1
        private const val GET_POSITION = 2
        private const val GET_WEIGHT = 3
        private const val GET_SLUG = 4
        private const val GET_VELOCITY = 5
        private const val SET_MOVEMENT = 6
        private const val SET_ROTATION = 7
        private const val ROTATE_ALONG_AXIS = 8
        private const val PRECISE_THRUST_IMPULSE = 9
    }

    private val attachedComputers = mutableSetOf<IComputerAccess>()

    override fun getType(): String = "ShipControlInterface"

    override fun getMethodNames(): Array<String> =
        arrayOf(
            "getRotation",
            "getMovement",
            "getPosition",
            "getWeight",
            "getSlug",
            "getVelocity",
            "setMovement",
            "setRotation",
            "rotateAlongAxis",
            "preciseThrustImpulse",
        )

    override fun callMethod(
        computer: IComputerAccess?,
        context: ILuaContext?,
        method: Int,
        arguments: IArguments?,
    ): MethodResult =
        when (method) {
            GET_ROTATION -> MethodResult.of(blockEntity.getRotation())
            GET_MOVEMENT -> MethodResult.of(blockEntity.getMovement())
            GET_POSITION -> MethodResult.of(blockEntity.getPosition())
            GET_WEIGHT -> MethodResult.of(blockEntity.getWeight())
            GET_SLUG -> MethodResult.of(blockEntity.getSlug())
            GET_VELOCITY -> MethodResult.of(blockEntity.getVelocity())
            SET_MOVEMENT -> {
                val (x, y, z) = getVec3(arguments, 3)
                blockEntity.setMovement(x, y, z)
                MethodResult.of(true)
            }
            SET_ROTATION -> {
                if (arguments == null || arguments.count() != 4) {
                    throw LuaException("setRotation requires 4 arguments")
                }
                blockEntity.setRotation(
                    arguments.getFiniteDouble(0),
                    arguments.getFiniteDouble(1),
                    arguments.getFiniteDouble(2),
                    arguments.getFiniteDouble(3),
                )
                MethodResult.of(true)
            }
            ROTATE_ALONG_AXIS -> {
                val (x, y, z) = getVec3(arguments, 3)
                blockEntity.rotateAlongAxis(x, y, z)
                MethodResult.of(true)
            }
            PRECISE_THRUST_IMPULSE -> {
                val (x, y, z) = getVec3(arguments, 3)
                blockEntity.preciseThrustImpulse(x, y, z)
                MethodResult.of(true)
            }
            else -> throw LuaException("Invalid method index $method")
        }

    override fun attach(computer: IComputerAccess) {
        attachedComputers.add(computer)
    }

    override fun detach(computer: IComputerAccess) {
        attachedComputers.remove(computer)
    }

    fun emitEvent(
        event: String,
        vararg args: Any?,
    ) {
        attachedComputers.forEach { it.queueEvent(event, *args) }
    }

    override fun equals(other: IPeripheral?): Boolean = other is ShipControlInterfacePeripheral && other.blockEntity == this.blockEntity

    override fun hashCode(): Int = blockEntity.hashCode()

    private fun getVec3(
        arguments: IArguments?,
        expectedCount: Int,
    ): Triple<Double, Double, Double> {
        // maybe used smw else? CC INTERFACE CONFIRMED??
        if (arguments == null || arguments.count() != expectedCount) {
            throw LuaException("Expected $expectedCount arguments")
        }
        return Triple(
            arguments.getFiniteDouble(0),
            arguments.getFiniteDouble(1),
            arguments.getFiniteDouble(2),
        )
    }
}
