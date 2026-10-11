package io.github.riiimc.easymaterial.material

import io.github.riiimc.easymaterial.EMRegistries
import io.github.riiimc.easymaterial.EasyMaterial
import io.github.riiimc.easymaterial.material.tag.MaterialTagRuntime
import net.mcexpanded.fancytabsections.FancyTabSections
import net.mcexpanded.fancytabsections.Section.SectionColored
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import java.util.function.Supplier

open class Material(
    val name: String,
    val color: Int,
    val items: MutableMap<String, DeferredItem<out Item>>,
    val blocks: MutableMap<String, DeferredHolder<Block, Block>>,
    val fluids: MutableMap<String, FluidData>
) {
    fun hasFluid(): Boolean { return fluids.isNotEmpty() }
    fun hasBlock(): Boolean { return blocks.isNotEmpty() }
    fun hasItem(): Boolean { return items.isNotEmpty() }

    companion object {
        internal val itemColors =
            mutableMapOf<DeferredHolder<Item, out Item>, Int>()

        internal val itemModels =
            mutableMapOf<ResourceLocation, ResourceLocation>()

        internal val blockColors =
            mutableMapOf<DeferredHolder<Block, Block>, Int>()
        internal val blockModels =
            mutableMapOf<ResourceLocation, ResourceLocation>()

    }

    open class Builder(
        private val material: String
    ) {
        private var color: Int = 0xFFFFFF
        //                             part,  item/block/fluid
        private val items: MutableMap<String, DeferredItem<out Item>> = mutableMapOf()
        private val blocks: MutableMap<String, DeferredHolder<Block, Block>> = mutableMapOf()
        private val fluids: MutableMap<String, FluidData> = mutableMapOf()

        fun color(color: Int): Builder {
            this.color = color
            return this
        }

        fun partWithBlock(part: String): Builder {
            val block = block(part, BlockBehaviour.Properties.of())
            item(part, Supplier {
                BlockItem(block.get(), Item.Properties())
            })
            return this
        }


        fun fluid(name: String): Builder {
            val id = "$material/$name"

            val type = EMRegistries.FLUID_TYPES.register(id, Supplier {
                FluidType(FluidType.Properties.create())
            })

            lateinit var prop: BaseFlowingFluid.Properties

            val source = EMRegistries.FLUIDS.register(id, Supplier {
                BaseFlowingFluid.Source(prop)
            })

            val flowing = EMRegistries.FLUIDS.register("${id}_flowing", Supplier {
                BaseFlowingFluid.Flowing(prop)
            })

            val fluidBlock = EMRegistries.BLOCKS.register(
                id,
                Supplier {
                    LiquidBlock(
                        source.get(),
                        BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                    )
                }
            )

            val bucket = EMRegistries.ITEMS.register(
                "${id}_bucket",
                Supplier {
                    BucketItem(
                        source.get(),
                        Item.Properties()
                            .stacksTo(1)
                            .craftRemainder(Items.BUCKET)
                    )
                }
            )
            items["${name}_bucket"] = bucket

            prop = BaseFlowingFluid.Properties(type, source, flowing)
                .block(fluidBlock)
                .bucket(bucket)

            fluids[name] = FluidData(
                type = type,
                source = source,
                flowing = flowing,
                prop = prop
            )

            val sourceId = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                id
            )

            val flowingId = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "${id}_flowing"
            )

            MaterialTagRuntime.registerFluid(
                material = material,
                part = name,
                fluidId = sourceId
            )

            MaterialTagRuntime.registerFluid(
                material = material,
                part = name,
                fluidId = flowingId
            )

            return this
        }


        fun block(
            name: String,
            behavior: BlockBehaviour.Properties,
            blockStateTemplate: String = name
        ): DeferredHolder<Block, Block> {
            val blockId = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "$material/$name"
            )

            val block = EMRegistries.BLOCKS.register(
                "$material/$name",
                Supplier { Block(behavior) }
            )

            blocks[name] = block

            EasyMaterialBlockStates.register(
                blockId,
                blockStateTemplate
            )

            blockModels[blockId] = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "block/$name"
            )

            MaterialTagRuntime.registerBlock(
                material = material,
                part = name,
                blockId = blockId
            )

            return block
        }

        fun parts(vararg parts: String): Builder {
            for (part in parts) {
                part(part)
            }
            return this
        }

        fun part(part: String): Builder {
            return part(part, Item.Properties())
        }

        fun part(part: String, prop: Item.Properties): Builder {
            item(part, prop)
            return this
        }

        private fun item(
            name: String,
            supplier: Supplier<out Item>
        ): DeferredItem<out Item> {
            val itemId = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "$material/$name"
            )

            val registeredItem = EMRegistries.ITEMS.register(
                "$material/$name",
                Supplier { supplier.get() }
            )

            items[name] = registeredItem

            itemModels[itemId] = ResourceLocation.fromNamespaceAndPath(
                EasyMaterial.MODID,
                "item/$name"
            )

            MaterialTagRuntime.registerItem(
                material = material,
                part = name,
                itemId = itemId
            )

            return registeredItem
        }

        private fun item(
            name: String,
            prop: Item.Properties
        ): DeferredItem<out Item> {
            return item(name, Supplier { Item(prop) })
        }

        fun build(): Material {
            items.values.forEach { item ->
                itemColors[item] = color
            }
            blocks.values.forEach { block ->
                blockColors[block] = color
            }
            val section = SectionColored(
                ResourceLocation.fromNamespaceAndPath(EasyMaterial.MODID, material)
            )
                .setBannerColor(color)
                .setTextColor(textColor(color))

            FancyTabSections.addSection(
                ResourceLocation.fromNamespaceAndPath(EasyMaterial.MODID, "tab"),
                section
            )

            items.values.forEach { holder ->
                section.add(holder)
            }
            return Material(material, color, items, blocks, fluids)
        }


        private fun textColor(backgroundColor: Int): Int {
            val r = (backgroundColor shr 16) and 0xFF
            val g = (backgroundColor shr 8) and 0xFF
            val b = backgroundColor and 0xFF

            val brightness = (r * 299 + g * 587 + b * 114) / 1000

            return if (brightness >= 128) {
                0x000000 // 明るい背景 → 黒文字
            } else {
                0xFFFFFF // 暗い背景 → 白文字
            }
        }
    }

    data class FluidData(
        val type: DeferredHolder<FluidType, FluidType>,
        val flowing: DeferredHolder<Fluid, BaseFlowingFluid.Flowing>,
        val source: DeferredHolder<Fluid, BaseFlowingFluid.Source>,
        val prop: BaseFlowingFluid.Properties
    )

}