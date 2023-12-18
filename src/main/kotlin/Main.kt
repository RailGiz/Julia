import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
fun JuliaSet() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawJuliaSet()
    }
}

fun DrawScope.drawJuliaSet() {
    val width = size.width.toInt()
    val height = size.height.toInt()
    val scale = 0.5f
    val movex = 0f
    val moveY = 0f
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
                topLeft = androidx.compose.ui.geometry.Offset(x.toFloat(), y.toFloat()),
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
