package nl.inholland.group9.karmakebab.data.models.shift

data class Task(
    val id: Int,
    val title: String,
    val requiresImage: Boolean = false,
    var isDone: Boolean = false
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Task? {
            return try {
                Task(
                    id = (map["id"] as? Long)?.toInt() ?: 0,
                    title = map["title"] as? String ?: "Untitled Task",
                    requiresImage = map["requiresImage"] as? Boolean ?: false,
                    isDone = false
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
