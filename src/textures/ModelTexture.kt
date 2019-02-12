package textures

class ModelTexture(val id: Int, val shineDamper: Float = 10F, val reflectivity: Float = 1F, val rows: Int = 1,
                   val hasTransparency: Boolean = false, val useFakeLighting: Boolean = false)