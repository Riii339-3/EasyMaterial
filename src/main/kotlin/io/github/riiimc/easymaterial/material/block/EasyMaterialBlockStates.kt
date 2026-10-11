package io.github.riiimc.easymaterial.material

import net.minecraft.resources.ResourceLocation
import java.util.concurrent.ConcurrentHashMap


/**
 * ブロック ID -> 共有 blockstates テンプレート名
 *
 * 例:
 * easymaterial:ruby/storage -> storage_block
 */
object EasyMaterialBlockStates {
    private val templates = ConcurrentHashMap<ResourceLocation, String>()

    @JvmStatic
    fun register(blockId: ResourceLocation, template: String) {
        require(template.matches(Regex("[a-z0-9/._-]+"))) {
            "Invalid blockstate template: $template"
        }
        require(!template.contains("..")) {
            "Invalid blockstate template path: $template"
        }

        templates[blockId] = template
    }

    internal fun snapshot(): Map<ResourceLocation, String> =
        templates.toMap()

    internal fun templateFor(blockId: ResourceLocation): String? =
        templates[blockId]
}