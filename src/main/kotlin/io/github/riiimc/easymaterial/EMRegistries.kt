package io.github.riiimc.easymaterial

import io.github.riiimc.easymaterial.example.Examples
import io.github.riiimc.easymaterial.material.MaterialUtils
import io.github.riiimc.easymaterial.material.event.MaterialRegistryEvent
import net.mcexpanded.fancytabsections.FancyTabSections
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object EMRegistries {
    val FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, EasyMaterial.MODID)
    val FLUIDS = DeferredRegister.create(Registries.FLUID, EasyMaterial.MODID)
    val ITEMS = DeferredRegister.createItems(EasyMaterial.MODID)
    val BLOCKS = DeferredRegister.createBlocks( EasyMaterial.MODID)



    fun event(bus: IEventBus) {
        val tab = FancyTabSections.registerCreativeModeTab(
            bus,
            ResourceLocation.fromNamespaceAndPath(EasyMaterial.MODID, "tab"),
            Items.IRON_INGOT
        )
        val event = MaterialRegistryEvent()


        println("[EasyMaterial] Posting custom event")
        bus.post(event)

        println("[EasyMaterial] materials: ${event.getMaterials().size}")

        MaterialUtils.addAll(event.getMaterials())
    }

    fun register(bus: IEventBus) {

        FLUID_TYPES.register(bus)
        FLUIDS.register(bus)
        ITEMS.register(bus)
        BLOCKS.register(bus)
    }
}