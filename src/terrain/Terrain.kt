package terrain

import models.RawModel
import org.lwjgl.util.vector.Vector3f
import render.Loader
import textures.TerrainTexture
import textures.TerrainTexturePack
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.lwjgl.util.vector.Vector2f
import utils.Maths


class Terrain(gridX: Int, gridZ: Int, val texturePack: TerrainTexturePack, val blendMap: TerrainTexture,
              heightMap: String, loader: Loader) {

    companion object {
        private const val SIZE = 800
        private const val MAX_HEIGHT = 40F
        private const val MAX_PIXEL_COLOUR = 256 * 256 * 256
    }

    val x: Float
    val z: Float
    val model: RawModel
    private lateinit var heights: Array<FloatArray>

    init {
        x = (gridX * SIZE).toFloat()
        z = (gridZ * SIZE).toFloat()
        model = generateTerrain(loader, heightMap)
    }

    private fun generateTerrain(loader: Loader, heightMap: String): RawModel {
        val image = ImageIO.read(File("res/$heightMap.png"))
        val vertexCount = image.height
        heights = Array(vertexCount) {FloatArray(vertexCount)}

        val count = vertexCount * vertexCount
        val vertices = FloatArray(count * 3)
        val normals = FloatArray(count * 3)
        val textureCoords = FloatArray(count * 2)
        val indices = IntArray(6 * (vertexCount - 1) * (vertexCount - 1))
        var vertexPointer = 0

        for (i in 0 until vertexCount) {
            for (j in 0 until vertexCount) {
                vertices[vertexPointer * 3] = j.toFloat() / (vertexCount.toFloat() - 1) * SIZE
                val height = calculateHeight(j, i, image)
                heights[j][i] = height
                vertices[vertexPointer * 3 + 1] = height
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (vertexCount.toFloat() - 1) * SIZE

                val normal = calculateNormal(j, i, image)
                normals[vertexPointer * 3] = normal.x
                normals[vertexPointer * 3 + 1] = normal.y
                normals[vertexPointer * 3 + 2] = normal.z

                textureCoords[vertexPointer * 2] = j.toFloat() / (vertexCount.toFloat() - 1)
                textureCoords[vertexPointer * 2 + 1] = i.toFloat() / (vertexCount.toFloat() - 1)
                vertexPointer++
            }
        }

        var pointer = 0
        for (gz in 0 until vertexCount - 1) {
            for (gx in 0 until vertexCount - 1) {
                val topLeft = gz * vertexCount + gx
                val topRight = topLeft + 1
                val bottomLeft = (gz + 1) * vertexCount + gx
                val bottomRight = bottomLeft + 1
                indices[pointer++] = topLeft
                indices[pointer++] = bottomLeft
                indices[pointer++] = topRight
                indices[pointer++] = topRight
                indices[pointer++] = bottomLeft
                indices[pointer++] = bottomRight
            }
        }
        return loader.loadToVao(vertices, textureCoords, normals, indices)
    }

    fun getHeight(worldX: Float, worldZ: Float): Float {
        val terrainX = worldX - x
        val terrainZ = worldZ - z

        val nSquares = (heights.size - 1).toFloat()
        val gridSquareSize = SIZE / nSquares

        val gridX = Math.floor((terrainX / gridSquareSize).toDouble()).toInt()
        val gridZ = Math.floor((terrainZ / gridSquareSize).toDouble()).toInt()

        if (gridX < 0 || gridX >= nSquares || gridZ < 0 || gridZ >= nSquares) {
            return 0F
        }

        val xCoord = (terrainX % gridSquareSize) / gridSquareSize
        val zCoord = (terrainZ % gridSquareSize) / gridSquareSize

        return if (xCoord <= 1 - zCoord) { // Determine which triangle coordinate in
            Maths.barycentric(Vector3f(0f, heights[gridX][gridZ], 0f),
                              Vector3f(1f, heights[gridX + 1][gridZ], 0f),
                              Vector3f(0f, heights[gridX][gridZ + 1], 1f), Vector2f(xCoord, zCoord)
                             )
        } else {
            Maths.barycentric(Vector3f(1f, heights[gridX + 1][gridZ], 0f),
                              Vector3f(1f, heights[gridX + 1][gridZ + 1], 1f),
                              Vector3f(0f, heights[gridX][gridZ + 1], 1f), Vector2f(xCoord, zCoord)
                             )
        }
    }

    private fun calculateHeight(x: Int, y: Int, image: BufferedImage): Float {
        if (x < 0 || x >= image.height || y < 0 || y >= image.height) {
            return 0F
        }

        var height = image.getRGB(x, y).toFloat()
        height += MAX_PIXEL_COLOUR / 2F
        height /= MAX_PIXEL_COLOUR / 2F // 0-1
        height *= MAX_HEIGHT
        return height
    }

    private fun calculateNormal(x: Int, y: Int, image: BufferedImage): Vector3f {
        val heightL = calculateHeight(x - 1, y, image)
        val heightR = calculateHeight(x + 1, y, image)
        val heightD = calculateHeight(x, y - 1, image)
        val heightU = calculateHeight(x, y + 1, image)

        val normal = Vector3f(heightL - heightR, 2F, heightD - heightU)
        normal.normalise()
        return normal
    }

}