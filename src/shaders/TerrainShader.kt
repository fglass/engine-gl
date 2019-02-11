package shaders

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import utils.Maths

class TerrainShader: ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {

    private var locationTransformationMatrix = 0
    private var locationProjectionMatrix = 0
    private var locationViewMatrix = 0
    private var locationLightPosition = 0
    private var locationLightColour = 0
    private var locationShineDamper = 0
    private var locationReflectivity = 0
    private var locationSkyColour = 0
    private var locationBgTexture = 0
    private var locationRTexture = 0
    private var locationGTexture = 0
    private var locationBTexture = 0
    private var locationBlendMap = 0

    companion object {
        private const val VERTEX_FILE = "src/shaders/terrain_vs.vert"
        private const val FRAGMENT_FILE = "src/shaders/terrain_fs.frag"
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix")
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix")
        locationViewMatrix = super.getUniformLocation("viewMatrix")
        locationLightPosition = super.getUniformLocation("lightPosition")
        locationLightColour = super.getUniformLocation("lightColour")
        locationShineDamper = super.getUniformLocation("shineDamper")
        locationReflectivity = super.getUniformLocation("reflectivity")
        locationSkyColour = super.getUniformLocation("skyColour")
        locationBgTexture = super.getUniformLocation("bgTexture")
        locationRTexture = super.getUniformLocation("rTexture")
        locationGTexture = super.getUniformLocation("gTexture")
        locationBTexture = super.getUniformLocation("bTexture")
        locationBlendMap = super.getUniformLocation("blendMap")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(locationTransformationMatrix, matrix)
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(locationProjectionMatrix, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val matrix = Maths.createViewMatrix(camera)
        super.loadMatrix(locationViewMatrix, matrix)
    }

    fun loadLight(light: Light) {
        super.loadVector(locationLightPosition, light.position)
        super.loadVector(locationLightColour, light.colour)
    }

    fun loadShineVariables(shineDamper: Float, reflectivity: Float) {
        super.loadFloat(locationShineDamper, shineDamper)
        super.loadFloat(locationReflectivity, reflectivity)
    }

    fun loadSkyColour(r: Float, g: Float, b: Float) {
        super.loadVector(locationSkyColour, Vector3f(r, g, b))
    }

    fun connectTextureUnits() {
        super.loadInt(locationBgTexture, 0)
        super.loadInt(locationRTexture, 1)
        super.loadInt(locationGTexture, 2)
        super.loadInt(locationBTexture, 3)
        super.loadInt(locationBlendMap, 4)
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoord")
        super.bindAttribute(2, "normal")
    }
}