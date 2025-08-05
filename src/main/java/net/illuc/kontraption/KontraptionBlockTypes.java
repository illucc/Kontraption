package net.illuc.kontraption;

import mekanism.api.math.FloatingLong;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeCustomSelectionBox;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismSounds;
import net.illuc.kontraption.blockEntities.*;
import net.illuc.kontraption.blockEntities.largehydrogen.TileEntityLiquidFuelThrusterCasing;
import net.illuc.kontraption.blockEntities.largehydrogen.TileEntityLiquidFuelThrusterExhaust;
import net.illuc.kontraption.blockEntities.largehydrogen.TileEntityLiquidFuelThrusterValve;
import net.illuc.kontraption.blockEntities.railgun.TileEntityRailgunCasing;
import net.illuc.kontraption.blockEntities.railgun.TileEntityRailgunCoil;
import net.illuc.kontraption.blockEntities.railgun.TileEntityRailgunController;
import net.illuc.kontraption.blockEntities.railgun.TileEntityRailgunPort;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KontraptionBlockTypes {

    private static final FloatingLong ION_THRUSTER_USAGE = FloatingLong.createConst(200);//FloatingLong.createConst(KontraptionConfigs.INSTANCE.getKontraption().getIonConsumption().get());
    private static final FloatingLong ION_THRUSTER_STORAGE = FloatingLong.createConst(200);////FloatingLong.createConst(KontraptionConfigs.INSTANCE.getKontraption().getIonConsumption().get());

    private KontraptionBlockTypes() {
    }

    public static final BlockTypeTile<TileEntityIonThruster> ION_THRUSTER = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.ION_THRUSTER, MekanismLang.HOLD_FOR_DESCRIPTION)
            .withEnergyConfig(() -> ION_THRUSTER_USAGE, () -> ION_THRUSTER_STORAGE)
            .withSound(MekanismSounds.CHARGEPAD) //change this
            .with(AttributeCustomSelectionBox.JSON)
            .withCustomShape(KontraptionBlockShapes.ION_THRUSTER)
            .with(new AttributeStateFacing(BlockStateProperties.FACING))
            .build();

    public static final BlockTypeTile<TileEntityShipControlInterface> SHIP_CONTROL_INTERFACE = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.SHIP_CONTROL_INTERFACE, MekanismLang.HOLD_FOR_DESCRIPTION)
            .withEnergyConfig(() -> ION_THRUSTER_USAGE, () -> ION_THRUSTER_STORAGE)
            .with(new AttributeStateFacing(BlockStateProperties.HORIZONTAL_FACING))
            .with(AttributeCustomSelectionBox.JSON)
            .withCustomShape(KontraptionBlockShapes.SHIP_CONTROL_INTERFACE)
            .build();

    public static final BlockTypeTile<TileEntityGyro> GYRO = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.GYRO, MekanismLang.HOLD_FOR_DESCRIPTION)
            .withEnergyConfig(() -> ION_THRUSTER_USAGE, () -> ION_THRUSTER_STORAGE)
            .with(new AttributeStateFacing(BlockStateProperties.HORIZONTAL_FACING))
            .with(AttributeCustomSelectionBox.JSON)
            .withCustomShape(KontraptionBlockShapes.GYRO)
            .build();

    public static final BlockTypeTile<TileEntityWheel> WHEEL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.WHEEL, MekanismLang.HOLD_FOR_DESCRIPTION)
            .withEnergyConfig(() -> ION_THRUSTER_USAGE, () -> ION_THRUSTER_STORAGE)
            //.withCustomShape(KontraptionBlockShapes.INSTANCE.getWHEEL())
            .with(new AttributeStateFacing(BlockStateProperties.FACING))
            .build();

    public static final BlockTypeTile<TileEntityLiquidFuelThrusterCasing> LIQUID_FUEL_THRUSTER_CASING = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.LIQUID_FUEL_THRUSTER_CASING, MekanismLang.HOLD_FOR_DESCRIPTION)
            //.withGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE, GeneratorsLang.TURBINE)
            .externalMultiblock()
            .build();

    public static final BlockTypeTile<TileEntityLiquidFuelThrusterValve> LIQUID_FUEL_THRUSTER_VALVE = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.LIQUID_FUEL_THRUSTER_VALVE, MekanismLang.HOLD_FOR_DESCRIPTION)
            //.withGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE, GeneratorsLang.TURBINE)
            .externalMultiblock()
            .build();

    public static final BlockTypeTile<TileEntityLiquidFuelThrusterExhaust> LIQUID_FUEL_THRUSTER_EXHAUST = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.LIQUID_FUEL_THRUSTER_EXHAUST, MekanismLang.HOLD_FOR_DESCRIPTION)
            //.withGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE, GeneratorsLang.TURBINE)
            .with(Attributes.INVENTORY, Attributes.COMPARATOR)
            .externalMultiblock()
            .build();


    public static final BlockTypeTile<TileEntityCannon> CANNON = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.CANNON, MekanismLang.HOLD_FOR_DESCRIPTION)
            //.withCustomShape(KontraptionBlockShapes.INSTANCE.getWHEEL())
            .with(new AttributeStateFacing(BlockStateProperties.FACING), Attributes.REDSTONE)
            .with(AttributeCustomSelectionBox.JSON)
            .withGui(() -> KontraptionContainerTypes.INSTANCE.getCANNON())
            .withCustomShape(KontraptionBlockShapes.CANNON)
            .withBounding((pos, state, builder) -> {
               builder.add(pos.relative(Attribute.getFacing(state)));
               builder.add(pos.offset(Attribute.getFacing(state).getNormal().multiply(2)));
               builder.add(pos.offset(Attribute.getFacing(state).getNormal().multiply(3)));
            })
            .build();

    public static final BlockTypeTile<TileEntityServo> SERVO = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.SERVO, MekanismLang.HOLD_FOR_DESCRIPTION)
            //.withCustomShape(KontraptionBlockShapes.INSTANCE.getWHEEL())
            .with(new AttributeStateFacing(BlockStateProperties.FACING))
            .build();

    public static final BlockTypeTile<TileEntityRailgunCoil> RAILGUN_COIL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.RAILGUN_COIL, MekanismLang.HOLD_FOR_DESCRIPTION)
            .externalMultiblock()
            .build();

    public static final BlockTypeTile<TileEntityRailgunCasing> RAILGUN_CASING = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.RAILGUN_CASING, MekanismLang.HOLD_FOR_DESCRIPTION)
            .externalMultiblock()
            .build();

    public static final BlockTypeTile<TileEntityRailgunController> RAILGUN_CONTROLLER = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.RAILGUN_CONTROLLER, MekanismLang.HOLD_FOR_DESCRIPTION)
            .externalMultiblock()
            .build();

    public static final BlockTypeTile<TileEntityRailgunPort> RAILGUN_PORT = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.RAILGUN_PORT, MekanismLang.HOLD_FOR_DESCRIPTION)
            .externalMultiblock()
            .build();
    public static final BlockTypeTile<TileEntityConnector> CONNECTOR = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.CONNECTOR, MekanismLang.HOLD_FOR_DESCRIPTION)
            .with(AttributeCustomSelectionBox.JSON)
            .withCustomShape(KontraptionBlockShapes.CONNECTOR)
            .with(new AttributeStateFacing(BlockStateProperties.FACING))
            .build();
    public static final BlockTypeTile<TileEntityKey> KEY = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.KEY, MekanismLang.HOLD_FOR_DESCRIPTION)
            .with(new AttributeStateFacing(BlockStateProperties.FACING))
            .build();
    public static final BlockTypeTile<TileEntityDrill> DRILL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> KontraptionTileEntityTypes.DRILL, MekanismLang.HOLD_FOR_DESCRIPTION)
            .with(new AttributeStateFacing(BlockStateProperties.FACING))
            .build();



}
