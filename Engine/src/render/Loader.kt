package render

import models.ModelData
import models.RawModel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.newdawn.slick.opengl.TextureLoader
import java.io.FileInputStream
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Loader {

    private val vaos = ArrayList<Int>()
    private val vbos =  ArrayList<Int>()
    private val textures = ArrayList<Int>()


    fun loadToVao(data: ModelData): RawModel {
        return loadToVao(data.vertices, data.textureCoords, data.normals, data.indices)
    }

    fun loadToVao(positions: FloatArray, textureCoords: FloatArray, normals: FloatArray, indices: IntArray): RawModel {
        val vaoId = createVAO()
        bindIndicesBufffer(indices)
        storeDataInAttribList(0, 3, positions)
        storeDataInAttribList(1, 2, textureCoords)
        storeDataInAttribList(2, 3, normals)
        unbindVAO()
        return RawModel(vaoId, indices.size)
    }

    fun loadTexture(filename: String): Int {
        val texture = TextureLoader.getTexture("PNG", FileInputStream("res/$filename.png"))

        // Mipmapping implementation
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4F)

        val textureId = texture.textureID
        textures.add(textureId)
        return textureId
    }

    fun cleanUp() {
        for (vao in vaos) {
            GL30.glDeleteVertexArrays(vao)
        }
        for (vbo in vbos) {
            GL15.glDeleteBuffers(vbo)
        }
        for (texture in textures) {
            GL11.glDeleteTextures(texture)
        }
    }

    private fun createVAO(): Int {
        val vaoId = GL30.glGenVertexArrays()
        vaos.add(vaoId)
        GL30.glBindVertexArray(vaoId)
        return vaoId
    }

    private fun storeDataInAttribList(attribNumber: Int, coordSize: Int, data: FloatArray) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        val buffer = storeDataInFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(attribNumber, coordSize, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0) // Unbind
    }

    private fun bindIndicesBufffer(indices: IntArray) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
        val buffer = storeDataInIntBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)

    }

    private fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    private fun storeDataInIntBuffer(data: IntArray): IntBuffer {
        val buffer = BufferUtils.createIntBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0) // Unbind currently bound VAO
    }
}