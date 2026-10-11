package io.github.riiimc.easymaterial.material.event

import io.github.riiimc.easymaterial.EasyMaterial
import io.github.riiimc.easymaterial.material.Material
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent

@EventBusSubscriber(
    modid = EasyMaterial.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object MaterialItemColors {

    @SubscribeEvent
    @JvmStatic
    fun register(event: RegisterColorHandlersEvent.Item) {
        Material.itemColors.forEach { (holder, color) ->
            val item = holder.get()

            event.register(
                { _, tintIndex ->
                    if (tintIndex == 0) {
                        color or (0xFF shl 24)
                    } else {
                        -1
                    }
                },
                item
            )
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun registerBlocks(event: RegisterColorHandlersEvent.Block) {
        Material.blockColors.forEach { (holder, color) ->
            event.register(
                { _, _, _, tintIndex ->
                    if (tintIndex == 0) color else -1
                },
                holder.get()
            )
        }
    }
    @SubscribeEvent
    @JvmStatic
    fun registerAdditionalB(event: ModelEvent.RegisterAdditional) {
        Material.blockModels.values.distinct().forEach { modelId ->
            event.register(ModelResourceLocation.standalone(modelId))
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun modifyBakingResultB(event: ModelEvent.ModifyBakingResult) {
        val models = event.models

        Material.blockModels.forEach { (blockId, modelId) ->
            val block = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(blockId)

            val sharedModelId = ModelResourceLocation.standalone(modelId)
            val sharedModel = models[sharedModelId] ?: return@forEach

            block.stateDefinition.possibleStates.forEach { state ->
                val stateModelId = net.minecraft.client.resources.model.ModelResourceLocation
                    .inventory(blockId)

                // ブロックのモデルIDはステートのプロパティを含むため、
                // inventory IDへの単純な差し替えでは対応できない。
            }
        }
    }

    @SubscribeEvent
    @JvmStatic
    fun registerAdditional(event: ModelEvent.RegisterAdditional) {
        Material.itemModels.values
            .distinct()
            .forEach { modelId ->
                event.register(
                    ModelResourceLocation.standalone(modelId)
                )
            }
    }


    @SubscribeEvent
    @JvmStatic
    fun modifyBakingResult(event: ModelEvent.ModifyBakingResult) {
        val models = event.models

        Material.itemModels.forEach { (itemId, modelId) ->
            val sharedModelId = ModelResourceLocation.standalone(modelId)
            val itemModelId = ModelResourceLocation.inventory(itemId)

            val sharedModel = models[sharedModelId]
            val originalModel = models[itemModelId]

            if (sharedModel != null) {
                models[itemModelId] = sharedModel

            }
        }
    }

}