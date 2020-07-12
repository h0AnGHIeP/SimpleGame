package client

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.net.Socket
import javax.swing.JFrame
import javax.swing.WindowConstants

class GameFrame(socket: Socket, name: String) : JFrame() {
    private val board = GameBoard(socket, name)

    init {
        add(board)
        isResizable = false
        pack()
        title = "Plane"
        setLocationRelativeTo(null)
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        addKeyListener(object : KeyListener {
            override fun keyTyped(p0: KeyEvent?) {
            }

            override fun keyPressed(p0: KeyEvent?) {
                board.changedControl = true
                when (p0?.keyCode) {
                    KeyEvent.VK_LEFT -> board.controlLeft = 1
                    KeyEvent.VK_RIGHT -> board.controlRight = 1
                    KeyEvent.VK_SPACE -> board.controlShoot = 1
                }
            }

            override fun keyReleased(p0: KeyEvent?) {
                board.changedControl = true
                when (p0?.keyCode) {
                    KeyEvent.VK_LEFT -> board.controlLeft = 0
                    KeyEvent.VK_RIGHT -> board.controlRight = 0
                    KeyEvent.VK_SPACE -> board.controlShoot = 0
                }
            }
        })

    }


}