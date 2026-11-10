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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalView
import android.view.Choreographer
import androidx.compose.material3.Button
import androidx.compose.runtime.withFrameNanos
import tw.edu.pu.csim.tcyang.canvas.ui.theme.CanvasTheme

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CanvasTheme {
        Greeting("Android")
    }
}

@Composable
fun Greeting(x0: String) {
    TODO("Not yet implemented")
}

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
    var gameOver by remember { mutableStateOf(false) }

    // 記錄圖片的 X 軸偏移量
    var imageOffsetX by remember { mutableStateOf(0f) }
    var imageOffsetY by remember { mutableStateOf(0f) }

    // 使用 LaunchedEffect 搭配每幀更新，達成每秒移動 10 像素的平順動畫
    LaunchedEffect(Unit) {
        var lastTimeNanos = 0L
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (lastTimeNanos != 0L) {
                    val elapsedSeconds = (frameTimeNanos - lastTimeNanos) / 1_000_000_000f
                    // 每秒移動 10 像素 (10f * 經過的時間)
                    imageOffsetX += 10f * elapsedSeconds
                }
                lastTimeNanos = frameTimeNanos
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text("畫布寬度 $CanvasWidth, 長度 $CanvasHeight")

        Button(
            onClick = {gameOver =!gameOver}
        ) {
            if (gameOver){Text("遊戲結束") }
    }

    val minionsImage: ImageBitmap =
        ImageBitmap.imageResource(id = R.drawable.minions)
    val path =
        Path().apply {
            moveTo(50f, 100f)  // 移動到起始點 (50f, 100f)
            lineTo(200f, 500f)  // 畫一條線到 (200f, 500f)
            lineTo(450f, 400f)  // 畫一條線到 (450f, 400f)
            close()  // 閉合路徑
        }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = modifier.fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> /* 手指按下 */ },
                        onDrag = { change, dragAmount -> /* 手指移動 */ },
                        onDragEnd = { /* 手指離開 */ }
                    )
                }
        ) {
            // 繪製洋紅色的圓, 半徑 100, 圓心 (100,100)
            drawCircle(
                Color.Magenta,
                100f,
                Offset(100f, 100f)
            )

            // 繪製藍色空心矩形, 左上角 (300,200), 寬度與高度 200
            drawRect(
                Color.Blue,
                Offset(300f, 200f),
                Size(200f, 200f),
                style = Stroke(width = 5f)
            )
            CanvasWidth = size.width
            CanvasHeight = size.height

            // 繪製紅色的線， 從 (200,800) 畫到右下角, 線條粗細 10
            drawLine(
                Color.Red,
                Offset(200f, 800f),
                Offset(size.width, size.height),
                strokeWidth = 10f
            )

            // 繪製綠色路徑，透明度1 (不透明)，線條粗細10
            drawPath(path, Color.Green, 1f, Stroke(width = 10f))

            // 取得小小兵圖檔的寬度與高度
            val minionsWidth = minionsImage.width.toFloat()
            val minionsHeight = minionsImage.height.toFloat()

            // 置中公式：(畫布寬高 - 圖片寬高) / 2f ，並加上移動的偏移量
            val offsetX = (size.width - minionsWidth) / 2f + imageOffsetX
            val offsetY = (size.height - minionsHeight) / 2f

            // 將畫布起點移至計算後的位置後畫圖
            translate(offsetX, offsetY) {
                drawImage(minionsImage)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}}