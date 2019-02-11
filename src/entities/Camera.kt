package entities

import org.lwjgl.input.Mouse
import org.lwjgl.util.vector.Vector3f

class Camera(private val player: Player) {

    val position = Vector3f(0F, 0F, 0F)
    var pitch = 20F
    var yaw = 0F
    val roll = 0F

    private var distanceFromPlayer = 50F
    private var angleAroundPlayer = 0F

    fun move() {
        calculateZoom()
        calculatePitch()
        calculateAngleAroundPlayer()

        val h = calculateHorizontalDistance()
        val v = calculateVerticalDistance()
        calculatePosition(h, v)

        yaw = 180 - (player.rotY + angleAroundPlayer)

    }

    private fun calculateZoom() {
        val zoomLevel = Mouse.getDWheel() * 0.1F
        distanceFromPlayer = Math.max(distanceFromPlayer - zoomLevel, 20F) // Lower limit
        distanceFromPlayer = Math.min(distanceFromPlayer, 200F) // Upper limit
    }

    private fun calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            val pitchChange = Mouse.getDY() * 0.1F
            pitch -= pitchChange
        }
    }

    private fun calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            val angleChange = Mouse.getDX() * 0.3F
            angleAroundPlayer -= angleChange
        }
    }

    private fun calculateHorizontalDistance(): Float {
        return (distanceFromPlayer * Math.cos(Math.toRadians(pitch.toDouble()))).toFloat()
    }

    private fun calculateVerticalDistance(): Float {
        return (distanceFromPlayer * Math.sin(Math.toRadians(pitch.toDouble()))).toFloat()
    }

    private fun calculatePosition(h: Float, v: Float) {
        val theta = player.rotY + angleAroundPlayer
        val xOffset = (h * Math.sin(Math.toRadians(theta.toDouble()))).toFloat()
        val zOffset = (h * Math.cos(Math.toRadians(theta.toDouble()))).toFloat()

        position.x = player.position.x - xOffset
        position.z = player.position.z - zOffset

        val chestOffset = 6
        position.y = player.position.y + v + chestOffset
    }
}