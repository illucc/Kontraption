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
    override fun getType(): String = "ShipControlInterface"

    public fun testingcc() { // Left bc i forgot to remove it and now im too lazy to change numbers :3
    }

    override fun getMethodNames(): Array<String> {
        return arrayOf("testingcc", "getRotation", "getMovement", "getPosition", "getWeight", "getSlug", "getVelocity", "setMovement", "setRotation", "rotateAlongAxis", "preciseThrustImpulse") // remember to remove testing method before adding other fun
    }

    override fun callMethod(
        computer: IComputerAccess?,
        context: ILuaContext?,
        method: Int,
        arguments: IArguments?,
    ): MethodResult {
        return when (method) {
            0 -> {
                testingcc()
                MethodResult.of("MF!!")
            }
            1 -> {
                return MethodResult.of(blockEntity.getRotation())
            }
            2 -> {
                return MethodResult.of(blockEntity.getMovement())
            }
            3 -> {
                return MethodResult.of(blockEntity.getPosition())
            }
            4 -> {
                return MethodResult.of(blockEntity.getWeight())
            }
            5 -> {
                return MethodResult.of(blockEntity.getSlug())
            }
            6 -> {
                return MethodResult.of(blockEntity.getVelocity())
            }
            7 -> {
                if (arguments?.count() != 3) {
                    throw LuaException("You must have 3 arguments.\n") // prob could be better
                }
                blockEntity.setMovement(
                    arguments?.getFiniteDouble(0) ?: 0.0,
                    arguments?.getFiniteDouble(1) ?: 0.0,
                    arguments?.getFiniteDouble(2) ?: 0.0,
                ) // AHH i love kotlin
                return MethodResult.of(true)
            }
            8 -> {
                if (arguments?.count() != 4) {
                    throw LuaException("You must have 4 arguments.\n")
                }
                blockEntity.setRotation(
                    arguments?.getFiniteDouble(0) ?: 0.0,
                    arguments?.getFiniteDouble(1) ?: 0.0,
                    arguments?.getFiniteDouble(2) ?: 0.0,
                    arguments?.getFiniteDouble(3) ?: 0.0,
                )
                return MethodResult.of(true)
            }
            9 -> {
                if (arguments?.count() != 3) {
                    throw LuaException("You must have 3 arguments.\n")
                }
                blockEntity.rotateAlongAxis(
                    arguments?.getFiniteDouble(0) ?: 0.0,
                    arguments?.getFiniteDouble(1) ?: 0.0,
                    arguments?.getFiniteDouble(2) ?: 0.0,
                )
                return MethodResult.of(true)
            }
            10 -> {
                if (arguments?.count() != 3) {
                    throw LuaException("You must have 3 arguments.\n")
                }
                blockEntity.preciseThrustImpulse(
                    arguments?.getFiniteDouble(0) ?: 0.0,
                    arguments?.getFiniteDouble(1) ?: 0.0,
                    arguments?.getFiniteDouble(2) ?: 0.0,
                )
                return MethodResult.of(true)
            }
            else -> MethodResult.of(null)
        }
    }

    override fun attach(computer: IComputerAccess) {}

    override fun detach(computer: IComputerAccess) {}

    override fun equals(other: IPeripheral?): Boolean = other is ShipControlInterfacePeripheral && other.blockEntity == this.blockEntity

    override fun hashCode(): Int = blockEntity.hashCode()
}
