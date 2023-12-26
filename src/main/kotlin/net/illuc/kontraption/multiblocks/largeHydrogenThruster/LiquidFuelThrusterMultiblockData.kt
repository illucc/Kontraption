package net.illuc.kontraption.multiblocks.largeHydrogenThruster

import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.chemical.attribute.ChemicalAttributeValidator
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.IGasTank
import mekanism.common.capabilities.chemical.multiblock.MultiblockChemicalTankBuilder
import mekanism.common.lib.multiblock.IValveHandler
import mekanism.common.lib.multiblock.MultiblockData
import mekanism.common.registries.MekanismGases
import net.illuc.kontraption.ThrusterInterface
import net.illuc.kontraption.blockEntities.TileEntityLiquidFuelThrusterCasing
import net.illuc.kontraption.config.KontraptionConfigs
import net.illuc.kontraption.particles.ThrusterParticleData
import net.illuc.kontraption.util.KontraptionVSUtils
import net.illuc.kontraption.util.toDoubles
import net.illuc.kontraption.util.toJOMLD
import net.illuc.kontraption.util.toMinecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.Ship
import java.util.function.LongSupplier
import java.util.function.Predicate


class LiquidFuelThrusterMultiblockData(tile: TileEntityLiquidFuelThrusterCasing) : MultiblockData(tile), ThrusterInterface, IValveHandler {

    // :cri:
    val te = tile
    var exhaustDirection: Direction = Direction.NORTH
    var centerExhaust: BlockEntity? = tile
    var exhaustDiameter = 0
    var offset: Vec3 = Vec3(0.0, 0.0, 0.0)
    var center: BlockPos = BlockPos(0, 0, 0)
    var innerVolume = 1

    var particleDir = exhaustDirection.normal.multiply(3+exhaustDiameter).toJOMLD()
    var pos = centerExhaust?.blockPos?.offset(exhaustDirection.normal.multiply(2))

    var ship: Ship? = null


    //----------------THRUSTER CONTROL-----------------------
    override var enabled = true
    override var thrusterLevel: Level? = centerExhaust?.level
    override var worldPosition: BlockPos? = center
    override var forceDirection: Direction = exhaustDirection.opposite
    override var powered: Boolean = true
    override var thrusterPower: Double = KontraptionConfigs.kontraption.liquidFuelThrust.get()
    override val basePower: Double = KontraptionConfigs.kontraption.liquidFuelThrust.get()

    //----------------stuff-----------------------


    var fuelTank: IGasTank? = null
    var burnRemaining = 0.0
    var lastBurnRate = 0.0

    init {
        LongSupplier { (thrusterPower * 100 * 4).toLong() }
        //fluidTanks.add(MultiblockFluidTank.create(10, tile))
        //fuelTank = MultiblockFluidTank.input(this, tile, { 10 }, { fluid: FluidStack -> MekanismTags.Fluids.LAVA_LOOKUP.contains(fluid.fluid) })
        gasTanks.add(MultiblockChemicalTankBuilder.GAS.input(this, { (thrusterPower * 100 * 4).toLong() }, { gas: Gas -> gas === MekanismGases.HYDROGEN.get() },
                ChemicalAttributeValidator.ALWAYS_ALLOW, null))
    }

    override fun onCreated(world: Level?) {


        super.onCreated(world)
        //smh my balls
        ship = KontraptionVSUtils.getShipObjectManagingPos((thrusterLevel as ServerLevel), center)
                ?: KontraptionVSUtils.getShipManagingPos((thrusterLevel as ServerLevel), center)
        offset = Vector3d(1.0, 1.0, 1.0)
                .add(exhaustDirection.normal.toJOMLD().normalize().negate())
                .mul(0.25 * exhaustDiameter)
                .add(exhaustDirection.normal.toJOMLD()
                        .mul(1.5)).toMinecraft()
        pos = centerExhaust?.blockPos?.offset(exhaustDirection.normal.multiply(1))

        thrusterPower = (KontraptionConfigs.kontraption.liquidFuelThrust.get() * innerVolume)
        if (ship != null) {
            thrusterLevel = centerExhaust?.level
            worldPosition = center
            forceDirection = exhaustDirection.opposite

            enable()
        }
    }


    override fun tick(world: Level?): Boolean {
        val needsPacket = super.tick(world)

        if (powered) {
            if (world != null) {
                burnFuel(world)
            }
        } else {
            lastBurnRate = 0.0;
        }

        if (powered and enabled){

            if (Dist.DEDICATED_SERVER.isDedicatedServer and (thrusterLevel != null)) {
                particleDir = if (ship == null){
                    exhaustDirection.normal.multiply(innerVolume).toJOMLD()
                }else {
                    ship!!.transform.shipToWorld.transformDirection(exhaustDirection.normal.multiply(innerVolume).toJOMLD())
                }

                //thrusterLevel as ServerLevel
                pos?.let { sendParticleData(thrusterLevel as ServerLevel, it.toDoubles(), particleDir) }
            }
        }
        return needsPacket
    }

    private fun burnFuel(world: Level) {
        val lastBurnRemaining: Double = burnRemaining
        var storedFuel: Double = fuelTank!!.stored + burnRemaining
        val toBurn = thrusterPower * KontraptionConfigs.kontraption.liquidFuelConsumption.get() //Math.min(Math.min(1.0, storedFuel), fuelAssemblies * MekanismGeneratorsConfig.generators.burnPerAssembly.get())
        storedFuel -= toBurn
        if (storedFuel <= 0.0){
            if (enabled == true){
                disable()
            }
        }else{
            if (enabled == false){
                enable()
            }
        }
        fuelTank!!.setStackSize(storedFuel.toLong(), Action.EXECUTE)
        burnRemaining = storedFuel % 1
        //heatCapacitor.handleHeat(toBurn * MekanismGeneratorsConfig.generators.energyPerFissionFuel.get().doubleValue())
        // update previous burn
        lastBurnRate = toBurn
    }


    private fun sendParticleData(level: Level, pos: Vec3, particleDir: Vector3d) {
        if (!isRemote && level is ServerLevel) {
            for (player in level.players()) {
                level.sendParticles(player, ThrusterParticleData(particleDir.x.toDouble(), particleDir.y.toDouble(), particleDir.z.toDouble(), innerVolume.toDouble()), true, pos.x+0.5, pos.y+0.5, pos.z+0.5, 2*exhaustDiameter, offset.x, offset.y, offset.z, 0.0)
            }
        }
    }

    fun getMaxFluid(): Int {
        return height() * 4 * 1
    }


}