package net.illuc.kontraption.debugger

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object DebugCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands
                .literal("dumpBakedReg")
                .executes { context ->
                    ModelRegistryDump.dumpBakedReg()
                    Command.SINGLE_SUCCESS
                },
        )
    }
}
