package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

open class Entity(val model: TexturedModel, val position: Vector3f, var rotX: Float, var rotY: Float, var rotZ: Float,
                  val scale: Float, private val textureIndex: Int = 0) {

    fun increasePosition(dx: Float, dy: Float, dz: Float) {
        position.x += dx
        position.y += dy
        position.z += dz
    }

    fun increaseRotation(dx: Float, dy: Float, dz: Float) {
        rotX += dx
        rotY += dy
        rotZ += dz
    }

    fun getTextureOffsetX(): Float {
        val column = textureIndex % model.texture.rows
        return column.toFloat() / model.texture.rows
    }

    fun getTextureOffsetY(): Float {
        val row = textureIndex / model.texture.rows
        return row.toFloat() / model.texture.rows
    }
}