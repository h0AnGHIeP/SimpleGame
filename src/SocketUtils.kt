import com.sun.jdi.connect.spi.ClosedConnectionException
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.nio.charset.Charset

fun Socket.sendLine(data: String) = getOutputStream().run {
    if (isClosed) throw ClosedConnectionException()
    write((data + "\n").toByteArray(Charset.defaultCharset()))
    flush()
}

fun Socket.readLine(): String {
    if (isClosed) throw ClosedConnectionException()
    val tempOutput = ByteArrayOutputStream()
    val input = getInputStream()
    try {
        var data = input.read()
        while (data != -1 && data != '\n'.toInt()) {
            tempOutput.write(data)
            data = input.read()
        }
        return tempOutput.toString(Charset.defaultCharset())
    } catch (e: Exception) {
        throw ClosedConnectionException()
    }
}


