package run

import entities.Camera
import entities.Entity
import entities.Light
import entities.Player
import models.ObjLoader
import models.TexturedModel
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Vector3f
import render.DisplayManager
import render.Loader
import render.MasterRenderer
import terrain.Terrain
import textures.ModelTexture
import textures.TerrainTexture
import textures.TerrainTexturePack
import java.util.*

fun main() {
    DisplayManager.createDisplay()

    val loader = Loader()
    val renderer = MasterRenderer()
    val light = Light(Vector3f(20000F, 40000F, 2000F), Vector3f(1F, 1F, 1F))

    val bgTexture = TerrainTexture(loader.loadTexture("grass"))
    val rTexture = TerrainTexture(loader.loadTexture("mud"))
    val gTexture = TerrainTexture(loader.loadTexture("flowers"))
    val bTexture = TerrainTexture(loader.loadTexture("path"))
    val texturePack = TerrainTexturePack(bgTexture, rTexture, gTexture, bTexture)
    val blendMap = TerrainTexture(loader.loadTexture("blendMap"))

    val terrain = Terrain(0, -1, texturePack, blendMap, "heightMap", loader)

    val playerModel = loader.loadToVao(ObjLoader.load("player"))
    val playerTexture = ModelTexture(loader.loadTexture("playerTexture"), reflectivity = 0.1F)
    val texturedPlayer = TexturedModel(playerModel, playerTexture)
    val player = Player(texturedPlayer, Vector3f(0F, 0F, -50F), 0F, 0F, 0F, 1F)
    val camera = Camera(player)

    val fern = loader.loadToVao(ObjLoader.load("fern"))
    val fernTexture = ModelTexture(loader.loadTexture("fern"), reflectivity = 0.1F, rows = 2,
                                   hasTransparency = true, useFakeLighting = true)
    val texturedFern = TexturedModel(fern, fernTexture)

    val tree = loader.loadToVao(ObjLoader.load("tree"))
    val texturedTree = TexturedModel(tree, ModelTexture(loader.loadTexture("tree"), reflectivity = 0.1F))

    val entities = ArrayList<Entity>()
    val random = Random()
    for (i in 0..50) { // TODO
        var x = random.nextFloat() * 800 - 400
        var z = random.nextFloat() * -400
        var y = terrain.getHeight(x, z)
        entities.add(Entity(texturedFern, Vector3f(x, y, z), 0F, 0F, 0F, 1F, random.nextInt(4)))

        x = random.nextFloat() * 800 - 400
        z = random.nextFloat() * -400
        y = terrain.getHeight(x, z)
        entities.add(Entity(texturedTree, Vector3f(x, y, z), 0F, 0F, 0F, 5F))
    }

    while (!Display.isCloseRequested()) {
        renderer.processTerrain(terrain)

        camera.move()
        player.move(terrain)
        renderer.processEntity(player)

        for (entity in entities) {
            renderer.processEntity(entity)
        }

        renderer.render(light, camera)
        DisplayManager.updateDisplay()
    }

    renderer.cleanUp()
    loader.cleanUp()
    DisplayManager.closeDisplay()
}
