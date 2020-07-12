package game

import game.strategy.Strategy

class EnemyObject(recentCoord: Coordinate = Coordinate(Game.STARTING_X, Game.B_Y),
                  oldCoord: Coordinate = Coordinate(Game.STARTING_X, Game.B_Y),
                  private val verticalDirection: VerticalDirection = VerticalDirection(
                          moveDown = true))
    : PlaneObject(recentCoord = recentCoord, oldCoord = oldCoord, isShooting = true) {
    override fun move() {
        super.move()
        recentCoord.apply {
            if (verticalDirection.moveDown) y = oldCoord.y + Game.ENEMY_VELOCITY
            if (verticalDirection.moveUp) y = oldCoord.y - Game.ENEMY_VELOCITY
            if (y < 0) y = 0
            if (y > Game.A_Y) alive = false
        }
    }

    fun detectCrashList(planes: MutableCollection<PlaneObject>) {
        planes.forEach {
            if (this.recentCoord.y >= Game.A_Y - 10 && detectCrashSingleX(it)) {
                it.alive = false
                this.alive = false
            }
        }
    }

    private fun detectCrashSingleX(plane: PlaneObject): Boolean {
        val oldX = plane.oldCoord.x
        val newX = plane.recentCoord.x
        return minOf(oldX, newX) - Game.PLANE_LENGTH <= recentCoord.x
                && recentCoord.x <= maxOf(oldX, newX) + Game.PLANE_LENGTH
    }
}