package io.github.riiimc.easymaterial.material

import io.github.riiimc.easymaterial.EasyMaterial
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.AbstractPackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.IoSupplier
import java.io.ByteArrayInputStream
import java.io.InputStream

internal class GeneratedBlockStateResources(
    location: PackLocationInfo
) : AbstractPackResources(location) {

    override fun getNamespaces(type: PackType): Set<String> {
        if (type != PackType.CLIENT_RESOURCES) return emptySet()

        return EasyMaterialBlockStates.snapshot()
            .keys
            .mapTo(mutableSetOf(EasyMaterial.MODID)) { it.namespace }
    }

    override fun getResource(
        packType: PackType,
        location: ResourceLocation
    ): IoSupplier<InputStream>? {
        if (packType != PackType.CLIENT_RESOURCES) return null

        val blockId = blockIdFromStateLocation(location) ?: return null
        val template = EasyMaterialBlockStates.templateFor(blockId) ?: return null
        val bytes = readTemplate(template) ?: return null

        return IoSupplier {
            ByteArrayInputStream(bytes)
        }
    }

    override fun listResources(
        packType: PackType,
        namespace: String,
        path: String,
        resourceOutput: net.minecraft.server.packs.PackResources.ResourceOutput
    ) {
        if (packType != PackType.CLIENT_RESOURCES) return

        for ((blockId, template) in EasyMaterialBlockStates.snapshot()) {
            if (blockId.namespace != namespace) continue

            val generatedLocation = ResourceLocation.fromNamespaceAndPath(
                blockId.namespace,
                "blockstates/${blockId.path}.json"
            )

            val generatedPath = generatedLocation.path
            if (generatedPath != path &&
                !generatedPath.startsWith("$path/")
            ) {
                continue
            }

            val bytes = readTemplate(template) ?: continue

            resourceOutput.accept(
                generatedLocation,
                IoSupplier { ByteArrayInputStream(bytes) }
            )
        }
    }

    override fun getRootResource(
        vararg elements: String
    ): IoSupplier<InputStream>? {
        if (!elements.contentEquals(arrayOf("pack.mcmeta"))) return null

        return IoSupplier {
            ByteArrayInputStream(PACK_META)
        }
    }

    override fun close() {
        // メモリ上のリソースのみなので、クローズ処理は不要。
    }

    private fun blockIdFromStateLocation(
        location: ResourceLocation
    ): ResourceLocation? {
        if (!location.path.startsWith("blockstates/")) return null
        if (!location.path.endsWith(".json")) return null

        val blockPath = location.path
            .removePrefix("blockstates/")
            .removeSuffix(".json")

        return runCatching {
            ResourceLocation.fromNamespaceAndPath(
                location.namespace,
                blockPath
            )
        }.getOrNull()
    }

    private fun readTemplate(template: String): ByteArray? {
        val path = "/assets/${EasyMaterial.MODID}/blockstates/$template.json"

        return try {
            GeneratedBlockStateResources::class.java
                .getResourceAsStream(path)
                ?.use { it.readBytes() }
        } catch (exception: Exception) {
            System.err.println(
                "[EasyMaterial] Failed to read blockstate template '$path': ${exception.message}"
            )
            null
        } ?: run {
            System.err.println(
                "[EasyMaterial] Missing blockstate template '$path'"
            )
            null
        }
    }

    companion object {
        private val PACK_META = """
            {
              "pack": {
                "pack_format": 34,
                "description": "EasyMaterial generated blockstates"
              }
            }
        """.trimIndent().toByteArray(Charsets.UTF_8)
    }
}