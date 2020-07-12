package game.strategy

import game.EnemyObject

interface Strategy {
    fun spawn(timeInSec: Double): List<EnemyObject>
    fun changeEnemySpeed(timeInSec: Double): Int = 6
    fun changePlaneSpeed(timeInSec: Double): Int = 3
}