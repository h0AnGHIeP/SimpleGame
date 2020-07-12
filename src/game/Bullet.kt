package game

class Bullet(val coord: Coordinate,
             var exist: Boolean = true,
             private val fromEnemy: Boolean = false) {

    //Move the bullet
    fun move() {
        if (fromEnemy) coord.y = coord.y + Game.BULLET_VELOCITY
        else coord.y = coord.y - Game.BULLET_VELOCITY
    }

    // True if crashed horizontally
    private fun detectCrashSingleX(plane: PlaneObject): Boolean {
        val oldX = plane.oldCoord.x
        val newX = plane.recentCoord.x
        return minOf(oldX, newX) - Game.PLANE_LENGTH <= coord.x
                && coord.x <= maxOf(oldX, newX) + Game.PLANE_LENGTH
    }

    // True if crashed vertically
    private fun detectCrashSingleY(plane: PlaneObject): Boolean {
        var oldY = plane.oldCoord.y
        var newY = plane.recentCoord.y
        if (oldY < newY) {
            oldY -= Game.PLANE_HEIGHT
            newY += Game.PLANE_HEIGHT
        } else {
            oldY += Game.PLANE_HEIGHT
            newY -= Game.PLANE_HEIGHT
        }
        return if (!fromEnemy) {
            oldY in (coord.y - Game.BULLET_VELOCITY)..coord.y ||
                    newY in (coord.y - Game.BULLET_VELOCITY)..coord.y

        } else {
            newY in coord.y..(coord.y + Game.BULLET_VELOCITY)
        }
    }

    // Detect crashing for the whole list
    fun detectCrashList(planes: MutableCollection<PlaneObject>) {
        planes.forEach {
            if (detectCrashSingleY(it) && detectCrashSingleX(it)) {
                exist = false
                it.alive = false
            }
        }
    }

    fun reachedEnd() = if (fromEnemy && coord.y > Game.A_Y) true
    else !fromEnemy && coord.y < 0

}