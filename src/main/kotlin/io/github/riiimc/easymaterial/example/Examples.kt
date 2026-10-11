package io.github.riiimc.easymaterial.example

import io.github.riiimc.easymaterial.EasyMaterial
import io.github.riiimc.easymaterial.material.Material
import io.github.riiimc.easymaterial.material.event.MaterialRegistryEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber

@EventBusSubscriber(modid = EasyMaterial.MODID, bus = EventBusSubscriber.Bus.MOD)
object Examples {
    @SubscribeEvent
    @JvmStatic
    fun onMaterialRegistryEvent(event: MaterialRegistryEvent) {
        event.register(
            Material.Builder("ruby")
                .color(-3735449)
                .fluid("molten")
                .partWithBlock("storage_blocks")
                .parts("gems", "nuggets", "dusts", "ingots", "gears", "plates")
                .part("rods")
                .build()
        )

        event.register(
            Material.Builder("sapphire")
                .color(1004218)
                .fluid("molten")
                .partWithBlock("storage_blocks")
                .parts("gems", "nuggets", "dusts", "ingots", "gears", "plates")
                .part("rods")
                .build()
        )

        event.register(
            Material.Builder("fantasy")
                .color(-4854313)
                .fluid("molten")
                .partWithBlock("storage_blocks")
                .parts("nuggets", "dusts", "ingots", "gears", "plates")
                .part("rods")
                .build()
        )

        event.register(
            Material.Builder("mythril")
                .color(-16711936)
                .fluid("molten")
                .partWithBlock("storage_blocks")
                .parts("nuggets", "dusts", "ingots", "gears", "plates")
                .part("rods")
                .build()
        )

        event.register(Material.Builder("mugen")
            .color(-18510)
            .fluid("molten")
            .partWithBlock("storage_blocks")
            .parts("gems", "nuggets", "dusts", "ingots", "gears", "plates")
            .part("rods")
            .build()
        )

        event.register(Material.Builder("adamantine")
            .color(-3682582)
            .fluid("molten")
            .partWithBlock("storage_blocks")
            .parts("gems", "nuggets", "dusts", "ingots", "gears", "plates")
            .part("rods")
            .build()
        )

    }
}