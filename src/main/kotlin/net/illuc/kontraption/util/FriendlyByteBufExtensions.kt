package net.illuc.kontraption.util

import net.illuc.kontraption.ship.KontraptionBConfigControl.BlockSetting
import net.illuc.kontraption.ship.KontraptionBConfigControl.ConfigBlock
import net.minecraft.network.FriendlyByteBuf

// TECHNICALLY AN GUI WELL NOT CLASS  BUT WELL

fun FriendlyByteBuf.writeConfigBlocks(blocks: List<ConfigBlock>) {
    writeVarInt(blocks.size)
    for (block in blocks) {
        writeBlockPos(block.pos.toBlockPos())
        writeVarInt(block.settings.size)

        for (setting in block.settings) {
            writeUtf(setting.name)

            when (setting) {
                is BlockSetting.BooleanSetting -> {
                    writeUtf("bool")
                    writeBoolean(setting.value)
                }
                is BlockSetting.IntSetting -> {
                    writeUtf("int")
                    writeVarInt(setting.value)
                }
                is BlockSetting.StringSetting -> {
                    writeUtf("string")
                    writeUtf(setting.value)
                }
                is BlockSetting.TittleSetting -> {
                    writeUtf("tittle")
                    writeUtf(setting.value)
                }
            }
        }
    }
}

fun FriendlyByteBuf.readConfigBlocks(): List<ConfigBlock> {
    val count = readVarInt()
    val blocks = mutableListOf<ConfigBlock>()

    repeat(count) {
        val pos = readBlockPos()
        val settingsCount = readVarInt()
        val settings = mutableListOf<BlockSetting>()

        repeat(settingsCount) {
            val name = readUtf()
            val typeId = readUtf()

            val setting =
                when (typeId) {
                    "bool" -> BlockSetting.BooleanSetting(name, readBoolean())
                    "int" -> BlockSetting.IntSetting(name, readVarInt())
                    "string" -> BlockSetting.StringSetting(name, readUtf())
                    "tittle" -> BlockSetting.TittleSetting(name, readUtf())
                    else -> throw IllegalArgumentException("Unknown Buffer Type: $typeId")
                }

            settings.add(setting)
        }

        blocks.add(ConfigBlock(pos.toJOML(), null, settings))
    }

    return blocks
}
