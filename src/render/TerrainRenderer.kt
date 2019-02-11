package render

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import shaders.TerrainShader
import terrain.Terrain
import utils.Maths

class TerrainRenderer(private val shader: TerrainShader, projectionMatrix: Matrix4f) {

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.connectTextureUnits()
        shader.stop()
    }

    fun render(terrains: MutableList<Terrain>) {
        for (terrain in terrains) {
            prepareTerrain(terrain)
            loadModelMatrix(terrain)
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.model.vertexCount, GL11.GL_UNSIGNED_INT, 0)
            unbind()
        }
    }

    private fun prepareTerrain(terrain: Terrain) {
        val rawModel = terrain.model
        GL30.glBindVertexArray(rawModel.vaoId)

        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        bindTextures(terrain)
        shader.loadShineVariables(1F, 0F)
    }

    private fun bindTextures(terrain: Terrain) {
        val texturePack = terrain.texturePack

        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.bgTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.rTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE2)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.gTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE3)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.bTexture.id)
        GL13.glActiveTexture(GL13.GL_TEXTURE4)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.blendMap.id)
    }

    private fun unbind() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    private fun loadModelMatrix(terrain: Terrain) {
        val transformationMatrix = Maths.createTransformationMatrix(Vector3f(terrain.x, 0F, terrain.z), 0F, 0F, 0F, 1F)
        shader.loadTransformationMatrix(transformationMatrix)
    }
}