package net.illuc.kontraption.debugger

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelManager
import net.minecraft.resources.ResourceLocation
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

object ModelRegistryDump {
    fun dumpBakedReg() { // Hate my life
        try {
            val modelManager: ModelManager = Minecraft.getInstance().modelManager

            val fields: Array<Field> = ModelManager::class.java.declaredFields
            for (field in fields) {
                if (field.genericType is ParameterizedType) {
                    val parameterizedType = field.genericType as ParameterizedType
                    val actualTypeArguments = parameterizedType.actualTypeArguments

                    if (actualTypeArguments.size == 2 &&
                        actualTypeArguments[0] == ResourceLocation::class.java &&
                        actualTypeArguments[1] == BakedModel::class.java
                    ) {
                        field.isAccessible = true

                        val bakedRegistry = field.get(modelManager) as? Map<ResourceLocation, BakedModel>
                        if (bakedRegistry != null) {
                            for ((modelLocation, model) in bakedRegistry) {
                                println("Model: $modelLocation -> Baked Model: $model")
                            }
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
