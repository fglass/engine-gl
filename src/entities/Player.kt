package entities

import models.TexturedModel
import org.lwjgl.input.Keyboard
import org.lwjgl.util.vector.Vector3f
import render.DisplayManager
import terrain.Terrain

class Player(model: TexturedModel, position: Vector3f, rotX: Float, rotY: Float, rotZ: Float, scale: Float) :
      Entity(model, position, rotX, rotY, rotZ, scale) {

    companion object {
        private const val RUN_SPEED = 40F
        private const val TURN_SPEED = 160F
        private const val JUMP_POWER = 30F
        private const val GRAVITY = -50F
        const val TERRAIN_HEIGHT = 0F
    }

    private var currentSpeed = 0F
    private var currentTurnSpeed = 0F
    private var upwardsSpeed = 0F
    private var isInAir = false

    fun move(terrain: Terrain) {
        checkInputs()

        val frameTime = DisplayManager.getFrameTime()
        increaseRotation(0F, currentTurnSpeed * frameTime, 0F)

        val distance = currentSpeed * frameTime
        val dx = (distance * Math.sin(Math.toRadians(rotY.toDouble()))).toFloat()
        val dz = (distance * Math.cos(Math.toRadians(rotY.toDouble()))).toFloat()

        upwardsSpeed += GRAVITY * frameTime
        increasePosition(dx, upwardsSpeed * frameTime, dz)

        val terrainHeight = terrain.getHeight(position.x, position.z)

        if (position.y < terrainHeight) {
            upwardsSpeed = 0F
            isInAir = false
            position.y = terrainHeight
        }
    }

    private fun checkInputs() {
        currentSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_W) -> RUN_SPEED
            Keyboard.isKeyDown(Keyboard.KEY_S) -> -RUN_SPEED
            else -> 0F
        }
        currentTurnSpeed = when {
            Keyboard.isKeyDown(Keyboard.KEY_D) -> -TURN_SPEED
            Keyboard.isKeyDown(Keyboard.KEY_A) -> TURN_SPEED
            else -> 0F
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                jump()
        }
    }

    private fun jump() {
        if (!isInAir) {
            upwardsSpeed = JUMP_POWER
            isInAir = true
        }
    }
}