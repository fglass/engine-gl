package models

import org.lwjgl.util.vector.Vector3f

class Vertex(val index: Int, val position: Vector3f) {

    companion object {
        private const val NO_INDEX = -1
    }

    var textureIndex = NO_INDEX
    var normalIndex = NO_INDEX
    val length: Float = position.length()
    var duplicateVertex: Vertex? = null

    fun isSet(): Boolean {
        return textureIndex != NO_INDEX && normalIndex != NO_INDEX
    }

    fun hasSameTextureAndNormal(otherTexture: Int, otherNormal: Int): Boolean {
		return otherTexture == textureIndex && otherNormal == normalIndex
    }
}