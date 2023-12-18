import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
//Двойной клик - увеличение
//Один клик передвигает
//Долгий клик - уменьшение
@Composable
fun JuliaSet() {
    var scale by remember { mutableStateOf(0.9f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { position ->
                    offsetX = position.x
                    offsetY = position.y
                    true
                },
                onLongPress = { position ->
                    offsetX = position.x
                    offsetY = position.y
                    scale /= 1.1f
                    println("Scale is now $scale")
                },
                onDoubleTap = { position ->
                    offsetX = position.x
                    offsetY = position.y
                    scale *= 1.1f
                    println("Scale is now $scale")
                }
            )
        }) {
        drawJuliaSet(scale, offsetX, offsetY)
    }
}

fun DrawScope.drawJuliaSet(scale: Float, offsetX: Float, offsetY: Float) {
    val width = size.width.toInt()
    val height = size.height.toInt()
    val movex = offsetX / width - 0.5f
    val moveY = offsetY / height - 0.5f
    val maxiterations = 300
    val cX = -0.7f
    val cY = 0.27015f

    for (x in 0 until width) {
        for (y in 0 until height) {
            var zx = 1.5f * (x - width / 2) / (0.5f * scale * width) + movex
            var zy = (y - height / 2) / (0.5f * scale * height) + moveY
            var i = maxiterations
            while (zx * zx + zy * zy < 4 && i > 0) {
                val tmp = zx * zx - zy * zy + cX
                zy = 2.0f * zx * zy + cY
                zx = tmp
                i--
            }
            val ratio = i.toFloat() / maxiterations
            drawRect(
                color = Color(ratio, ratio, ratio, 1f),
                topLeft = Offset(x.toFloat(), y.toFloat()),
                size = androidx.compose.ui.geometry.Size(1f, 1f)
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        JuliaSet()
    }
}
