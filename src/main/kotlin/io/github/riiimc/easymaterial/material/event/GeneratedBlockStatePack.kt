package io.github.riiimc.easymaterial.material.event

import io.github.riiimc.easymaterial.EasyMaterial
import io.github.riiimc.easymaterial.material.GeneratedBlockStateResources
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AddPackFindersEvent
import java.util.Optional

@EventBusSubscriber(
    modid = EasyMaterial.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object GeneratedBlockStatePack {

    @SubscribeEvent
    @JvmStatic
    fun addPackFinder(event: AddPackFindersEvent) {
        if (event.packType != PackType.CLIENT_RESOURCES) return

        event.addRepositorySource { packConsumer ->
            val location = PackLocationInfo(
                "easymaterial:generated_blockstates",
                Component.literal("EasyMaterial generated blockstates"),
                PackSource.BUILT_IN,
                Optional.empty()
            )

            val supplier = object : Pack.ResourcesSupplier {
                override fun openPrimary(
                    location: PackLocationInfo
                ): PackResources =
                    GeneratedBlockStateResources(location)

                override fun openFull(
                    location: PackLocationInfo,
                    metadata: Pack.Metadata
                ): PackResources =
                    GeneratedBlockStateResources(location)
            }

            val pack = Pack.readMetaAndCreate(
                location,
                supplier,
                PackType.CLIENT_RESOURCES,
                PackSelectionConfig(
                    true,
                    Pack.Position.TOP,
                    true
                )
            )

            if (pack != null) {
                packConsumer.accept(pack)
            } else {
                System.err.println(
                    "[EasyMaterial] Failed to create generated blockstate pack"
                )
            }
        }
    }
}