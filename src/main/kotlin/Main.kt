import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*

@Composable
fun JuliaSet() {
    var scale by remember { mutableStateOf(0.9f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var drawData by remember { mutableStateOf(listOf<Pair<Offset, Color>>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = scale, key2 = offsetX, key3 = offsetY) {
        isLoading = true
        val startTime = System.currentTimeMillis()
        drawData = calculateJuliaSet(scale, offsetX, offsetY)
        val endTime = System.currentTimeMillis()
        println("Time taken: ${endTime - startTime} ms")
        isLoading = false
    }

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
        if (!isLoading) {
            for ((offset, color) in drawData) {
                drawRect(color = color, topLeft = offset, size = Size(1f, 1f))
            }
        }
    }
}

suspend fun calculateJuliaSet(scale: Float, offsetX: Float, offsetY: Float): List<Pair<Offset, Color>> {
    return withContext(Dispatchers.Default) {
        val width = 1928
        val height = 1080
        val movex = offsetX / width - 0.5f
        val moveY = offsetY / height - 0.5f
        val maxiterations = 300
        val cX = -0.7f
        val cY = 0.27015f
        val drawData = mutableListOf<Pair<Offset, Color>>()

        val numThreads = 11
        val jobs = List(numThreads) { i ->
            launch {
                val startY = i * height / numThreads
                val endY = (i + 1) * height / numThreads
                for (x in 0 until width) {
                    for (y in startY until endY) {
                        var zx = 1.5f * (x - width / 2) / (0.5f * scale * width) + movex
                        var zy = (y - height / 2) / (0.5f * scale * height) + moveY
                        var iter = maxiterations
                        while (zx * zx + zy * zy < 4 && iter > 0) {
                            val tmp = zx * zx - zy * zy + cX
                            zy = 2.0f * zx * zy + cY
                            zx = tmp
                            iter--
                        }
                        val ratio = iter.toFloat() / maxiterations
                        synchronized(drawData) {
                            drawData.add(Pair(Offset(x.toFloat(), y.toFloat()), Color(ratio, ratio, ratio, 1f)))
                        }
                    }
                }
            }
        }
        jobs.forEach { it.join() }
        drawData
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        JuliaSet()
    }
}
