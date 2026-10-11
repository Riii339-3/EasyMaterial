package io.github.riiimc.easymaterial.material.tag

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.AbstractPackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.resources.IoSupplier
import java.io.ByteArrayInputStream
import java.io.InputStream

internal class RuntimeTagPackResources(
    location: PackLocationInfo
) : AbstractPackResources(location) {

    private fun resources(): Map<ResourceLocation, ByteArray> =
        MaterialTagRuntime.createResources()

    override fun getNamespaces(type: PackType): Set<String> {
        if (type != PackType.SERVER_DATA) return emptySet()
        return resources().keys.mapTo(mutableSetOf()) { it.namespace }
    }

    override fun getResource(
        packType: PackType,
        location: ResourceLocation
    ): IoSupplier<InputStream>? {
        if (packType != PackType.SERVER_DATA) return null

        val bytes = resources()[location] ?: return null
        return IoSupplier { ByteArrayInputStream(bytes) }
    }

    override fun listResources(
        packType: PackType,
        namespace: String,
        path: String,
        resourceOutput: PackResources.ResourceOutput
    ) {
        if (packType != PackType.SERVER_DATA) return

        resources().forEach { (id, bytes) ->
            if (id.namespace != namespace) return@forEach

            if (id.path == path || id.path.startsWith("$path/")) {
                resourceOutput.accept(
                    id,
                    IoSupplier { ByteArrayInputStream(bytes) }
                )
            }
        }
    }

    override fun getRootResource(
        vararg elements: String
    ): IoSupplier<InputStream>? {
        if (!elements.contentEquals(arrayOf("pack.mcmeta"))) return null

        val metadata = """
            {
              "pack": {
                "pack_format": 48,
                "description": "EasyMaterial runtime tags"
              }
            }
        """.trimIndent().toByteArray(Charsets.UTF_8)

        return IoSupplier { ByteArrayInputStream(metadata) }
    }

    override fun close() = Unit
}