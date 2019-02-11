package render

import entities.Camera
import entities.Entity
import entities.Light
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import shaders.StaticShader
import shaders.TerrainShader
import terrain.Terrain

class MasterRenderer {

    private lateinit var projectionMatrix: Matrix4f

    private val staticShader = StaticShader()
    private val entityRenderer: EntityRenderer
    private val terrainShader = TerrainShader()
    private val terrainRenderer: TerrainRenderer

    private val entities = HashMap<TexturedModel, MutableList<Entity>>()
    private val terrains = ArrayList<Terrain>()


    companion object {
        const val FOV = 70F
        const val NEAR_PLANE = 0.1F
        const val FAR_PLANE = 1000F

        // Sky colour
        const val RED = 0.5F
        const val GREEN = 0.5F
        const val BLUE = 0.5F

        fun enableCulling() {
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glCullFace(GL11.GL_BACK)
        }

        fun disableCulling() {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
    }

    init {
        enableCulling()
        createProjectionMatrix()
        entityRenderer = EntityRenderer(staticShader, projectionMatrix)
        terrainRenderer = TerrainRenderer(terrainShader, projectionMatrix)
    }

    fun render(sun: Light, camera: Camera) {
        prepare()

        staticShader.start()
        staticShader.loadSkyColour(RED, GREEN, BLUE)
        staticShader.loadLight(sun)
        staticShader.loadViewMatrix(camera)
        entityRenderer.render(entities)
        staticShader.stop()

        terrainShader.start()
        terrainShader.loadSkyColour(RED, GREEN, BLUE)
        terrainShader.loadLight(sun)
        terrainShader.loadViewMatrix(camera)
        terrainRenderer.render(terrains)
        terrainShader.stop()

        entities.clear()
        terrains.clear()
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GL11.glClearColor(RED, GREEN, BLUE, 1F)
    }

    fun processEntity(entity: Entity) {
        val model = entity.model
        val batch = entities[model]

        if (batch != null) {
            batch.add(entity)
        } else {
            val newBatch = ArrayList<Entity>()
            newBatch.add(entity)
            entities[model] = newBatch
        }
    }

    fun processTerrain(terrain: Terrain) {
        terrains.add(terrain)
    }

    private fun createProjectionMatrix() {
        val aspectRatio = Display.getWidth().toFloat() / Display.getHeight().toFloat()
        val yScale = ((1F / Math.tan(Math.toRadians((FOV / 2F).toDouble()))) * aspectRatio).toFloat()
        val xScale = yScale / aspectRatio
        val frustumLength = FAR_PLANE - NEAR_PLANE

        projectionMatrix = Matrix4f()
        projectionMatrix.m00 = xScale
        projectionMatrix.m11 = yScale
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength)
        projectionMatrix.m23 = -1F
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength)
        projectionMatrix.m33 = 0f
    }

    fun cleanUp() {
        staticShader.cleanUp()
        terrainShader.cleanUp()
    }
}