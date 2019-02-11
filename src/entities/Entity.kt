package entities

import models.TexturedModel
import org.lwjgl.util.vector.Vector3f

open class Entity(val model: TexturedModel, val position: Vector3f, var rotX: Float, var rotY: Float, var rotZ: Float,
                  val scale: Float) {

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
}