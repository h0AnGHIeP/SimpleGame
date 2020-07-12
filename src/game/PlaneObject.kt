package game


open class PlaneObject(var lastTimeShoot: Long = 0L,
                       var alive: Boolean = true,
                       var isShooting: Boolean = false,
                       val direction: Direction = Direction(),
                       val recentCoord: Coordinate = Coordinate(Game.STARTING_X,
                               Game.A_Y),
                       val oldCoord: Coordinate = Coordinate(Game.STARTING_X, Game.A_Y)) {

    // Update coordination
    open fun move() {
        oldCoord.x = recentCoord.x
        oldCoord.y = recentCoord.y
        recentCoord.apply {
            if (direction.moveLeft) x = oldCoord.x - Game.PLANE_VELOCITY
            if (direction.moveRight) x = oldCoord.x + Game.PLANE_VELOCITY
            if (x > Game.MAX_WIDTH) x = Game.MAX_WIDTH
            if (x < 0) x = 0
        }

    }

    fun shootAvailable(): Boolean {
        return System.currentTimeMillis() - lastTimeShoot >= Game.RELOAD_TIME
    }
}