package client

import game.Game
import readLine
import sendLine
import java.awt.*
import java.net.Socket
import java.util.*
import javax.swing.ImageIcon
import javax.swing.JPanel

class GameBoard(private val socket: Socket, private val playerName: String) : JPanel(),
        Runnable {
    companion object {
        const val BOARD_WIDTH = 350
        const val BOARD_HEIGHT = 350
    }

    private val planeImg: Image
    private val bulletImg: Image
    private val enemyImg: Image
    private lateinit var animator: Thread
    private var gameData = ""

    var controlLeft = 0
    var controlRight = 0
    var controlShoot = 0
    var changedControl = true
    private var isOver = false

    private var timeDied = 0L

    init {
        background = Color.WHITE
        preferredSize = Dimension(BOARD_WIDTH, BOARD_HEIGHT)
        planeImg = ImageIcon("icons8.png").image
        bulletImg = ImageIcon("bullet.png").image
        enemyImg = ImageIcon("enemy.png").image
    }


    override fun addNotify() {
        super.addNotify()
        animator = Thread(this)
        animator.start()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        drawPlane(g!!)
        if (isOver) g.apply {
            val font = g.font.deriveFont(30F)
            setFont(font)
            drawString("DIED", Game.MAX_WIDTH / 2 - 20, Game.A_Y / 2 + 30)
        }
    }

    private fun drawPlane(g: Graphics) {
        if (gameData.isNotBlank()) {
            val dataToken = StringTokenizer(gameData, "||")
            val planes = StringTokenizer(dataToken.nextToken(), ",")

            while (planes.hasMoreTokens()) {
                val plane = StringTokenizer(planes.nextToken())
                val name = plane.nextToken()
                if (plane.nextToken().toInt() == 1) {
                    drawPlane(plane.nextToken().toInt(),
                            plane.nextToken().toInt(),
                            name,
                            g)
                    if (name == playerName) isOver = false
                } else {
                    if (name == playerName) isOver = true
                }
            }

            if (dataToken.hasMoreTokens()) {
                val bullets = StringTokenizer(dataToken.nextToken(), ",")
                while (bullets.hasMoreTokens()) {
                    val bullet = StringTokenizer(bullets.nextToken())
                    drawBullet(bullet.nextToken().toInt(), bullet.nextToken().toInt(), g)
                }
            }
        }

        Toolkit.getDefaultToolkit().sync()
    }

    private fun drawPlane(x: Int, y: Int, name: String, g: Graphics) {
        if (y != Game.A_Y || name == "enemy") g.drawImage(enemyImg, x, y, this)
        else g.apply {
            drawImage(planeImg, x, y, this@GameBoard)
            drawString(name, x + 10, y + 55)
            if (name == playerName) drawString("YOU", x + 10, y + 70)
        }
    }


    private fun drawBullet(x: Int, y: Int, g: Graphics) {
        g.drawImage(bulletImg, x, y, this)
    }

    override fun run() {
        while (true) {
            if (socket.getInputStream().available() > 0) {
                gameData = socket.readLine()
                if (changedControl) {
                    signalServer()
                    changedControl = false
                }
            }
            repaint()
        }
    }

    private fun signalServer() {
        socket.sendLine("$controlLeft $controlRight $controlShoot")
    }

}