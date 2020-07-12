package game

import game.strategy.DefaultStrategy
import manager.Player
import readLine
import sendLine
import java.util.*
import java.util.concurrent.ExecutorService

class Game(private val executor: ExecutorService) {
    companion object {
        // Length of the plane
        const val PLANE_LENGTH = 15
        const val PLANE_HEIGHT = 5

        // Y coordination of B side
        const val A_Y = 250

        // Y coordination of B side
        const val B_Y = 0

        // Y coordination of B side
        const val STARTING_X = 0

        // Time delay for each frame
        const val TIME_INTERVAL = 10L

        // Distance plane can move in Single Time Unit
        var PLANE_VELOCITY = 3

        // Distance bullet can move in Single Time Unit
        const val BULLET_VELOCITY = 15

        // Minimum time between two fires
        const val RELOAD_TIME = 1000L

        // Width of the Game Frame
        const val MAX_WIDTH = 300

        // Enemy plane velocity
        var ENEMY_VELOCITY = 6
        const val SPAWN_INTERVAL = 500L
    }

    private val bulletList = mutableListOf<Bullet>()
    val playerMap = mutableMapOf<Player, PlaneObject>()
    private val enemyPlanes = mutableListOf<EnemyObject>()
    private val strategy = DefaultStrategy()
    private var allDied = true

    init {
        // Run the game
        Thread {
            while (true) {
                if (allDied) reset()
                playerMap.keys.forEach {
                    // Read command if available
                    val input = it.socket.getInputStream()
                    if (input.available() > 0) {
                        try {
                            val command = StringTokenizer(it.socket.readLine())
                            if (command.countTokens() == 3) {
                                playerMap[it]!!.apply {
                                    direction.moveLeft = command.nextToken().toInt() == 1
                                    direction.moveRight = command.nextToken().toInt() == 1
                                    isShooting = command.nextToken().toInt() == 1
                                }
                            } else {
                                exit(it)
                            }
                        } catch (e: Exception) {
                            removePlayer(it)
                        }
                    }
                }
                refresh()
                Thread.sleep(TIME_INTERVAL)
            }
        }.start()
    }

    private fun reset() {
        enemyPlanes.clear()
        bulletList.clear()
        startUpTime = 0.0
        PLANE_VELOCITY = 3
        ENEMY_VELOCITY = 6
        playerMap.values.forEach {
            it.alive = true
        }
        Thread.sleep(5000L)
    }

    private fun exit(player: Player) {
        playerMap.remove(player)
    }

    fun addPlayer(player: Player) {
        synchronized(playerMap) {
            playerMap[player] = PlaneObject(alive = false)
        }
    }

    private var current = System.currentTimeMillis()
    private var previous = current

    private var spawn = current
    private var startUpTime = 0.0

    // Function called for every game'frames
    private fun refresh() {
        // Create enemy planes
        current = System.currentTimeMillis()
        if (current - spawn >= SPAWN_INTERVAL) {
            startUpTime += 0.5
            spawn = current
            val enemies = strategy.spawn(startUpTime)
            enemyPlanes.addAll(enemies)
            strategy.apply {
                PLANE_VELOCITY = changePlaneSpeed(startUpTime)
                ENEMY_VELOCITY = changeEnemySpeed(startUpTime)
            }
        }
        // Move the plane and create bullets
        allDied = true
        playerMap.forEach {
            val plane = it.value
            if (plane.alive) {
                allDied = false
                plane.move()
                if (plane.isShooting && plane.shootAvailable()) {
                    bulletList.add(Bullet(Coordinate(plane.recentCoord.x,
                            plane.recentCoord.y - PLANE_HEIGHT - 1)))
                    plane.lastTimeShoot = current
                }
            }
        }
        // Move the ENEMY plane and create bullets
        enemyPlanes.forEach {
            it.move()
            it.detectCrashList(playerMap.values)
        }
        // Move the bullets and check crashing
        bulletList.forEach {
            it.move()
            it.detectCrashList(enemyPlanes.toMutableList())
            if (it.reachedEnd()) it.exist = false
        }

        removeRedundantBullets()
        removeEnemyObjects()

        // Send the final data
        val data = gameData()
        playerMap.forEach {
            executor.submit {
                try {
                    it.key.socket.sendLine(data)
                } catch (e: Exception) {
                    removePlayer(it.key)
                }
            }
        }

        previous = current

    }

    // Return game data in String form
    private fun gameData() = buildString {
        playerMap.forEach {
            append("${it.key.name} ${if (it.value.alive) 1 else 0} ${it.value.recentCoord.x} ${it.value.recentCoord.y},")
        }
        enemyPlanes.forEach {
            append("enemy ${if (it.alive) 1 else 0} ${it.recentCoord.x} ${it.recentCoord.y},")
        }
        append("||")
        bulletList.forEach {
            append("${it.coord.x} ${it.coord.y},")
        }
    }

    // Remove player and close socket
    private fun removePlayer(player: Player) {
        synchronized(playerMap) {
            player.socket.close()
            playerMap.remove(player)
        }
    }

    // Remove non-exist bullets
    private fun removeRedundantBullets() {
        var x = 0
        while (x < bulletList.size) {
            if (!bulletList[x].exist) bulletList.removeAt(x)
            else x++
        }
    }

    private fun removeEnemyObjects() {
        var x = 0
        while (x < enemyPlanes.size) {
            if (!enemyPlanes[x].alive) enemyPlanes.removeAt(x)
            else x++
        }
    }
}