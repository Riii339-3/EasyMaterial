package io.github.riiimc.easymaterial.material

object MaterialUtils {
    private val materials = mutableListOf<Material>()

    fun add(material: Material) {
        materials += material
    }

    fun addAll(materials: Collection<Material>) {
        this.materials.addAll(materials)
    }

    fun getMaterials(): List<Material> = materials.toList()
}