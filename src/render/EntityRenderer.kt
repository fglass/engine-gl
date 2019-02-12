package render

import entities.Entity
import models.TexturedModel
import org.lwjgl.opengl.*
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import utils.Maths

class EntityRenderer(private val shader: StaticShader, projectionMatrix: Matrix4f) {

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(entities: HashMap<TexturedModel, MutableList<Entity>>) {
        for (item in entities) {
            val model = item.key
            prepareTexturedModel(model)

            val batch = item.value
            for (entity in batch) {
                prepareInstance(entity)
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.rawModel.vertexCount, GL11.GL_UNSIGNED_INT, 0)
            }
            unbindTexturedModel()
        }
    }

    private fun prepareTexturedModel(model: TexturedModel) {
        val rawModel = model.rawModel
        GL30.glBindVertexArray(rawModel.vaoId)

        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)

        val texture = model.texture
        if (texture.hasTransparency) {
            MasterRenderer.disableCulling()
        }

        shader.loadRows(texture.rows)
        shader.loadFakeLighting(texture.useFakeLighting)
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id)
    }

    private fun unbindTexturedModel() {
        MasterRenderer.enableCulling()
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    private fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTransformationMatrix(
            entity.position, entity.rotX, entity.rotY, entity.rotZ, entity.scale
        )
        shader.loadTransformationMatrix(transformationMatrix)
        shader.loadOffset(entity.getTextureOffsetX(), entity.getTextureOffsetY())
    }
}