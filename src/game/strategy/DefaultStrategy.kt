package game.strategy

import game.EnemyObject
import game.Game

class DefaultStrategy : Strategy {
    override fun spawn(timeInSec: Double): List<EnemyObject> {
        val newOne = EnemyObject().apply {
            isShooting = false
            recentCoord.x = (Math.random() * Game.MAX_WIDTH).toInt()
            recentCoord.y = 0
        }
        val list = mutableListOf(newOne)
        if (timeInSec > 12) {
            val secondOne = EnemyObject().apply {
                isShooting = false
                recentCoord.x = (Math.random() * Game.MAX_WIDTH).toInt()
                recentCoord.y = 0
            }
            list.add(secondOne)
        }
        return list
    }

    override fun changeEnemySpeed(timeInSec: Double): Int {
        return when {
            timeInSec > 3 -> 8
            timeInSec > 6 -> 10
            timeInSec > 9 -> 12
            timeInSec > 12 -> 14
            timeInSec > 16 -> 17
            else -> super.changeEnemySpeed(timeInSec)
        }
    }

    override fun changePlaneSpeed(timeInSec: Double): Int {
        return when {
            timeInSec > 6 -> 5
            timeInSec > 12 -> 7
            timeInSec > 18 -> 10
            else -> super.changePlaneSpeed(timeInSec)
        }
    }
}