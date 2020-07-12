package manager

import game.Game
import readLine
import sendLine
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingDeque


class GameManager(private val executor: ExecutorService,
                  private val server: ServerSocket) {
    private val playerList = LinkedBlockingDeque<Player>()
    private val gameList = mutableListOf(Game(executor))
    private var nConnection = 0

    init {

        // Thread run to establish connections
        Thread {
            while (true) {
                val playerSock = server.accept()
                val newPlayer = Player("No name", UUID.randomUUID(), playerSock)
                executor.submit {
                    accept(newPlayer)
                }

            }
        }.start()

        // Thread run to collect command from players
        Thread {
            while (true) {
                playerList.forEach {
                    try {
                        val input = it.socket.getInputStream()
                        if (input.available() > 0) {
                            val command = StringTokenizer(it.socket.readLine())
                            val name = command.nextToken()
                            val gameNumber = command.nextToken().toInt()
                            if (gameNumber > gameList.size) refuse(it)
                            else {
                                it.name = name
                                join(it, gameList[gameNumber - 1])
                            }
                        }
                    } catch (e: NumberFormatException) {
                        refuse(it)
                    } catch (e: Exception) {
                        removePlayer(it)
                    }
                }
                if (nConnection / 7 > gameList.size) gameList.add(Game(executor))
            }
        }.start()
    }

    // Accept player's connection
    private fun accept(player: Player) {
        val header = "ACCEPT,${player.id}," + gamesData()
        playerList.add(player)
        player.socket.sendLine(header)
        nConnection++
    }

    // Return Games'data in String form
    private fun gamesData() = buildString {
        append("GAMES,")
        gameList.forEach {
            append(it.playerMap.size.toString() + ",")
        }
    }

    // Close socket and remove player
    private fun removePlayer(player: Player) {
        player.socket.close()
        playerList.remove(player)
        nConnection--
    }

    // Allow player to join the room
    private fun join(player: Player, game: Game) {
        if (game.playerMap.size < 8) {
            game.addPlayer(player)
            playerList.remove(player)
            player.socket.sendLine("ACCEPTED")
        } else refuse(player)
    }

    // Send refuse message
    private fun refuse(player: Player) {
        player.socket.sendLine("REFUSED")
    }

}