package render

import org.lwjgl.Sys
import org.lwjgl.opengl.*

class DisplayManager {

    companion object {

        private const val WIDTH  = 1280
        private const val HEIGHT = 720
        private const val FPS_CAP = 60
        private const val TITLE = "Engine"

        private var lastFrameTime = 0L
        private var delta = 0F

        fun createDisplay() {
            Display.setDisplayMode(DisplayMode(WIDTH, HEIGHT))
            val attribs = ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true)
            Display.create(PixelFormat(), attribs)
            Display.setTitle(TITLE)
            GL11.glViewport(0, 0, WIDTH, HEIGHT)
            lastFrameTime = getCurrentTime()
        }

        fun updateDisplay() {
            Display.sync(FPS_CAP)
            Display.update()

            val currentFrameTime = getCurrentTime()
            delta = (currentFrameTime - lastFrameTime) / 1000F
            lastFrameTime = currentFrameTime
        }

        fun closeDisplay() {
            Display.destroy()
        }

        fun getFrameTime(): Float {
            return delta
        }

        private fun getCurrentTime(): Long {
            return Sys.getTime() * 1000 / Sys.getTimerResolution()
        }
    }
}