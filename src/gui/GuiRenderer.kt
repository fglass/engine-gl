package gui

import models.RawModel
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import render.Loader
import shaders.GuiShader
import utils.Maths

class GuiRenderer(loader: Loader) {

    private val quad: RawModel
    private val shader: GuiShader

    init {
        val positions = floatArrayOf(-1F, 1F, -1F, -1F, 1F, 1F, 1F, -1F)
        quad = loader.loadToVao(positions)
        shader = GuiShader()
    }

    fun render(sprites: List<Sprite>) {
        shader.start()
        GL30.glBindVertexArray(quad.vaoId)
        GL20.glEnableVertexAttribArray(0)

        // Alpha blending
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glDisable(GL11.GL_DEPTH_TEST) // Allow sprite overlapping

        for (sprite in sprites) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.texture)
            val matrix = Maths.createTransformationMatrix(sprite.position, sprite.scale)
            shader.loadTransformationMatrix(matrix)
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount)
        }

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        shader.stop()
    }

    fun cleanUp() {
        shader.cleanUp()
    }
}