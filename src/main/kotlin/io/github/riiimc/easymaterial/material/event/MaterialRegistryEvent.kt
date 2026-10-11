package io.github.riiimc.easymaterial.material.event

import io.github.riiimc.easymaterial.material.Material
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.IModBusEvent

class MaterialRegistryEvent : Event(), IModBusEvent {
    private val materials = mutableListOf<Material>()

    fun register(material: Material) {
        materials += material
    }

    fun getMaterials(): List<Material> = materials.toList()
}