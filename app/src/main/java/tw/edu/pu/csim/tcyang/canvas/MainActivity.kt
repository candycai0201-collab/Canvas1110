package tw.edu.pu.csim.tcyang.canvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.withFrameNanos
import tw.edu.pu.csim.tcyang.canvas.ui.theme.CanvasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TestCanvas(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TestCanvas(modifier: Modifier = Modifier) {
    var CanvasWidth by remember { mutableStateOf(value = 0f) }
    var CanvasHeight by remember { mutableStateOf(value = 0f) }

    // 記錄圖片的 X 軸與 Y 軸偏移量
    var imageOffsetX by remember { mutableStateOf(0f) }
    var imageOffsetY by remember { mutableStateOf(0f) }

    // 控制動畫暫停與繼續的狀態
    var isRunning by remember { mutableStateOf(true) }

    val minionsImage: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.minions)
    val minionsWidth = minionsImage.width.toFloat()
    val minionsHeight = minionsImage.height.toFloat()

    // 處理每秒移動 10 像素與邊界重置邏輯
    LaunchedEffect(isRunning) {
        if (isRunning) {
            var lastTimeNanos = 0L
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    if (lastTimeNanos != 0L) {
                        val elapsedSeconds = (frameTimeNanos - lastTimeNanos) / 1_000_000_000f
                        imageOffsetX += 30f * elapsedSeconds
                        imageOffsetY += 30f * elapsedSeconds
                    }
                    lastTimeNanos = frameTimeNanos
                }

                // 當圖片完全超出畫布（圖片左上角位置 >= 畫布寬高 + 圖片自身的寬高）時，立刻重置回 (0, 0)
                if (CanvasWidth > 0f && CanvasHeight > 0f) {
                    if (imageOffsetX >= CanvasWidth + minionsWidth || imageOffsetY >= CanvasHeight + minionsHeight) {
                        imageOffsetX = 0f
                        imageOffsetY = 0f
                    }
                }
            }
        }
    }

    val path = Path().apply {
        moveTo(50f, 100f)
        lineTo(200f, 500f)
        lineTo(450f, 400f)
        close()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. 底層：Canvas 畫布與圖形
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> },
                        onDrag = { change, dragAmount -> },
                        onDragEnd = {}
                    )
                }
        ) {
            drawCircle(
                Color.Magenta,
                100f,
                Offset(100f, 100f)
            )

            drawRect(
                Color.Blue,
                Offset(300f, 200f),
                Size(200f, 200f),
                style = Stroke(width = 5f)
            )

            CanvasWidth = size.width
            CanvasHeight = size.height

            drawLine(
                Color.Red,
                Offset(200f, 800f),
                Offset(size.width, size.height),
                strokeWidth = 10f
            )

            drawPath(path, Color.Green, 1f, Stroke(width = 10f))

            // 從左上角 (0, 0) 開始跑，加上偏移量
            val offsetX = imageOffsetX
            val offsetY = imageOffsetY

            translate(offsetX, offsetY) {
                drawImage(minionsImage)
            }
        }

        // 2. 上層：文字顯示在左上方
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text("畫布寬度 $CanvasWidth, 長度 $CanvasHeight")
        }

        // 3. 上層：按鈕固定在右上角（TopEnd）
        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            Text(if (isRunning) "暫停" else "繼續")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CanvasTheme {
        Greeting("Android")
    }
}