package io.github.riiimc.easymaterial.client

import io.github.riiimc.easymaterial.EasyMaterial
import io.github.riiimc.easymaterial.material.MaterialUtils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent

@EventBusSubscriber(
    modid = EasyMaterial.MODID,
    bus = EventBusSubscriber.Bus.MOD
)
object RegisterFluidModel {

    @SubscribeEvent
    @JvmStatic
    fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        MaterialUtils.getMaterials().forEach { material ->
            val fluidData = material.fluids.values.firstOrNull()
                ?: return@forEach

            val holder = BuiltInRegistries.FLUID.getHolderOrThrow(
                fluidData.source.key
            )
            val still = holder.key?.location() ?: return@forEach
            val flowing_ = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                still.path + "_flowing"
            )

            val base = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "base_fluid"
            )
            val flowing = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "flowing_fluid"
            )

            event.registerFluidType(
                object : IClientFluidTypeExtensions {
                    override fun getStillTexture(): ResourceLocation = base

                    override fun getFlowingTexture(): ResourceLocation = flowing

                    override fun getTintColor(): Int = material.color
                },
                fluidData.type
            )
        }

    }
}