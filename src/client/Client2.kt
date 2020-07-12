package client


import readLine
import sendLine
import java.awt.EventQueue
import java.net.Socket
import java.util.*

fun main() {
    val sock = Socket("localhost", 8000)
    val code = StringTokenizer(sock.readLine(), ",")
    var n = 1
    if (code.nextToken() == "ACCEPT") {
        println("OK")
        println("Player ID: '${code.nextToken()}'")
        code.nextToken()
        println("Room existed")
        while (code.hasMoreTokens()) {
            println("ROOM $n : ${code.nextToken()}")
            n++
        }
    }
    var rightFormat = false
    var name = ""
    var roomNum = 1
    val scanner = Scanner(System.`in`)
    while (!rightFormat) {
        try {
            print("Input your name : ")
            name = scanner.next()
            print("Which room ?? : ")
            roomNum = scanner.nextInt()
            rightFormat = true
        } catch (e: Exception) {
            rightFormat = false
        }
        if (!rightFormat) println("please input right format")
    }
    sock.sendLine("$name $roomNum")
    while (sock.readLine() != "ACCEPTED") {
        println("Room does not exist!!")
        print("Which room, again ?? : ")
        roomNum = scanner.nextInt()
        sock.sendLine("$name $roomNum")
    }
    println("========    ENJOY THE GAME   ===========")
    EventQueue.invokeLater {
        GameFrame(sock,name).isVisible = true
    }
}
