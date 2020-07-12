import manager.GameManager
import java.net.ServerSocket
import java.util.concurrent.Executors

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = ServerSocket(8000)
        val exec = Executors.newFixedThreadPool(2)
        GameManager(exec, server)
    }
}


