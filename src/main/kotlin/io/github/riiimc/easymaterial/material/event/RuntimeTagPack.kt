package io.github.riiimc.easymaterial.material.event

import io.github.riiimc.easymaterial.EasyMaterial
import io.github.riiimc.easymaterial.material.tag.RuntimeTagPackResources
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.PackSelectionConfig
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AddPackFindersEvent
import java.util.Optional

@EventBusSubscriber(
    modid = EasyMaterial.MODID,
    bus = EventBusSubscriber.Bus.MOD
)
object RuntimeTagPack {

    @SubscribeEvent
    @JvmStatic
    fun addPack(event: AddPackFindersEvent) {
        if (event.packType != PackType.SERVER_DATA) return

        event.addRepositorySource { packConsumer ->
            val location = PackLocationInfo(
                "easymaterial:runtime_tags",
                Component.literal("EasyMaterial Runtime Tags"),
                PackSource.BUILT_IN,
                Optional.empty()
            )

            val supplier = object : Pack.ResourcesSupplier {
                override fun openPrimary(
                    location: PackLocationInfo
                ): net.minecraft.server.packs.PackResources =
                    RuntimeTagPackResources(location)

                override fun openFull(
                    location: PackLocationInfo,
                    metadata: Pack.Metadata
                ): net.minecraft.server.packs.PackResources =
                    RuntimeTagPackResources(location)
            }

            val pack = Pack.readMetaAndCreate(
                location,
                supplier,
                PackType.SERVER_DATA,
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
                    "[EasyMaterial] Failed to create runtime tag pack"
                )
            }
        }
    }
}