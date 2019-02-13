package shaders

import org.lwjgl.util.vector.Matrix4f

class GuiShader: ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {

    private var locationTransformationMatrix = 0

    companion object {
        private const val VERTEX_FILE = "src/shaders/gui_vs.vert"
        private const val FRAGMENT_FILE = "src/shaders/gui_fs.frag"
    }

    override fun getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        loadMatrix(locationTransformationMatrix, matrix)
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
    }
}