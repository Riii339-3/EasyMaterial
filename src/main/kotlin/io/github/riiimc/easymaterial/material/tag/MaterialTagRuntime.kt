package io.github.riiimc.easymaterial.material.tag

import io.github.riiimc.easymaterial.EasyMaterial
import net.minecraft.resources.ResourceLocation
import java.util.LinkedHashMap
import java.util.LinkedHashSet

object MaterialTagRuntime {
    private data class Entry(
        val registryType: String,
        val material: String,
        val part: String,
        val value: ResourceLocation
    )

    private val entries = mutableListOf<Entry>()

    @Synchronized
    private fun register(
        registryType: String,
        material: String,
        part: String,
        value: ResourceLocation
    ) {
        require(registryType in setOf("item", "block", "fluid"))
        require(part.matches(Regex("[a-z0-9/._-]+")) && !part.contains("..")) {
            "Invalid tag part: $part"
        }

        val entry = Entry(registryType, material, part, value)
        if (entry !in entries) {
            entries += entry
        }
    }

    @JvmStatic
    fun registerItem(
        material: String,
        part: String,
        itemId: ResourceLocation
    ) = register("item", material, part, itemId)

    @JvmStatic
    fun registerBlock(
        material: String,
        part: String,
        blockId: ResourceLocation
    ) = register("block", material, part, blockId)

    @JvmStatic
    fun registerFluid(
        material: String,
        part: String,
        fluidId: ResourceLocation
    ) = register("fluid", material, part, fluidId)

    /**
     * ResourceLocation -> JSON bytes
     *
     * ResourceLocation は data/<namespace>/ 以下の相対パス。
     */
    @Synchronized
    internal fun createResources(): Map<ResourceLocation, ByteArray> {
        val grouped =
            LinkedHashMap<ResourceLocation, LinkedHashSet<ResourceLocation>>()

        for (entry in entries) {
            // easymaterial:ruby/ores
            val ownTag = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "${entry.material}/${entry.part}"
            )

            // c:ores/ruby
            val commonTag = ResourceLocation.fromNamespaceAndPath(
                "c",
                "${entry.part}/${entry.material}"
            )
            val commonTag2 = ResourceLocation.fromNamespaceAndPath(
                "c",
                entry.part
            )

            addValue(grouped, entry.registryType, ownTag, entry.value)
            addValue(grouped, entry.registryType, commonTag, entry.value)
            addValue(grouped, entry.registryType, commonTag2, entry.value)
        }

        return grouped.map { (resourceId, values) ->
            val json = buildString {
                append("{\"replace\":false,\"values\":[")
                append(
                    values.joinToString(",") { value ->
                        "\"" + value.toString() + "\""
                    }
                )
                append("]}")
            }

            resourceId to json.toByteArray(Charsets.UTF_8)
        }.toMap()
    }

    private fun addValue(
        output: MutableMap<ResourceLocation, LinkedHashSet<ResourceLocation>>,
        registryType: String,
        tagId: ResourceLocation,
        value: ResourceLocation
    ) {
        val resourceId = ResourceLocation.fromNamespaceAndPath(
            tagId.namespace,
            "tags/$registryType/${tagId.path}.json"
        )

        output.getOrPut(resourceId) { LinkedHashSet() }.add(value)
    }
}