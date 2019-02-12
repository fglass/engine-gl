package shaders

import entities.Camera
import entities.Light
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import utils.Maths

class StaticShader: ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {

    private var locationTransformationMatrix = 0
    private var locationProjectionMatrix = 0
    private var locationViewMatrix = 0
    private var locationLightPosition = 0
    private var locationLightColour = 0
    private var locationShineDamper = 0
    private var locationReflectivity = 0
    private var locationUseFakeLighting = 0
    private var locationSkyColour = 0
    private var locationRows = 0
    private var locationOffset = 0

    companion object {
        private const val VERTEX_FILE = "src/shaders/vs.vert"
        private const val FRAGMENT_FILE = "src/shaders/fs.frag"
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix")
        locationProjectionMatrix = getUniformLocation("projectionMatrix")
        locationViewMatrix = getUniformLocation("viewMatrix")
        locationLightPosition = getUniformLocation("lightPosition")
        locationLightColour = getUniformLocation("lightColour")
        locationShineDamper = getUniformLocation("shineDamper")
        locationReflectivity = getUniformLocation("reflectivity")
        locationUseFakeLighting = getUniformLocation("useFakeLighting")
        locationSkyColour = getUniformLocation("skyColour")
        locationRows = getUniformLocation("rows")
        locationOffset = getUniformLocation("offset")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        loadMatrix(locationTransformationMatrix, matrix)
    }

    fun loadProjectionMatrix(matrix: Matrix4f) {
        loadMatrix(locationProjectionMatrix, matrix)
    }

    fun loadViewMatrix(camera: Camera) {
        val matrix = Maths.createViewMatrix(camera)
        loadMatrix(locationViewMatrix, matrix)
    }

    fun loadLight(light: Light) {
        loadVector(locationLightPosition, light.position)
        loadVector(locationLightColour, light.colour)
    }

    fun loadShineVariables(shineDamper: Float, reflectivity: Float) {
        loadFloat(locationShineDamper, shineDamper)
        loadFloat(locationReflectivity, reflectivity)
    }

    fun loadFakeLighting(use: Boolean) {
        loadBoolean(locationUseFakeLighting, use)
    }

    fun loadSkyColour(r: Float, g: Float, b: Float) {
        loadVector(locationSkyColour, Vector3f(r, g, b))
    }

    fun loadRows(rows: Int) {
        loadInt(locationRows, rows)
    }

    fun loadOffset(x: Float, y: Float) {
        load2DVector(locationOffset, Vector2f(x, y))
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoord")
        bindAttribute(2, "normal")
    }
}