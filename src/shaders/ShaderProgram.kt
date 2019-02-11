package shaders

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

abstract class ShaderProgram(vertexFile: String, fragmentFile: String) {

    private var programId = 0
    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    init {
        vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER)
        fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER)
        programId = GL20.glCreateProgram()
        GL20.glAttachShader(programId, vertexShaderId)
        GL20.glAttachShader(programId, fragmentShaderId)
        bindAttributes()
        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)
        getAllUniformLocations()
    }

    fun start() {
        GL20.glUseProgram(programId)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun cleanUp() {
        stop()
        GL20.glDetachShader(programId, vertexShaderId)
        GL20.glDetachShader(programId, fragmentShaderId)
        GL20.glDeleteShader(vertexShaderId)
        GL20.glDeleteShader(fragmentShaderId)
        GL20.glDeleteProgram(programId)
    }

    fun getUniformLocation(name: String): Int {
        return GL20.glGetUniformLocation(programId, name)
    }

    abstract fun getAllUniformLocations()

    fun bindAttribute(attribute: Int, variableName: String) {
        GL20.glBindAttribLocation(programId, attribute, variableName)
    }

    abstract fun bindAttributes()

    fun loadInt(location: Int, value: Int) {
        GL20.glUniform1i(location, value)
    }

    fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    fun loadVector(location: Int, vector: Vector3f) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z)
    }

    fun loadBoolean(location: Int, value: Boolean) {
        var toLoad = 0F
        if (value) toLoad = 1F
        GL20.glUniform1f(location, toLoad)

    }

    fun loadMatrix(location: Int, matrix: Matrix4f) {
        matrix.store(matrixBuffer)
        matrixBuffer.flip()
        GL20.glUniformMatrix4(location, false, matrixBuffer)
    }

    companion object {

        private val matrixBuffer = BufferUtils.createFloatBuffer(16)

        fun loadShader(file: String, type: Int): Int {
            val shaderSource = StringBuilder()
            try {
                val reader = BufferedReader(FileReader(file))
                for (line in reader.lines()) {
                    shaderSource.append(line).append("\n")
                }
                reader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val shaderId = GL20.glCreateShader(type)
            GL20.glShaderSource(shaderId, shaderSource)
            GL20.glCompileShader(shaderId)

            if (GL20.glGetShader(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                println(GL20.glGetShaderInfoLog(shaderId, 500))
                System.exit(-1)
            }

            return shaderId
        }
    }
}