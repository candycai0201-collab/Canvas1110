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
    var CanvasWidth by remember { mutableStateOf(value = 0f)}
    var CanvasHeight by remember { mutableStateOf(value = 0f) }

    Column(modifier = modifier.fillMaxSize()) {
        Text("畫布寬度 $CanvasWidth, 長度 $CanvasHeight")
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
                    // 監聽拖曳手勢
                    detectDragGestures(
                        onDragStart = { offset -> /* 手指按下 */ },
                        onDrag = { change, dragAmount -> /* 手指移動 */ },
                        onDragEnd = { /* 手指離開 */ }
                    )
                }
        ) {
            //繪製洋紅色的圓, 半徑 100, 圓心 (100,100)
            drawCircle(
                Color.Magenta,
                100f,
                Offset(100f, 100f)
            )

            //繪製藍色空心矩形, 左上角 (300,200), 寬度與高度 200
            drawRect(
                Color.Blue,
                Offset(300f, 200f),
                Size(200f, 200f),
                style = Stroke(width = 5f) // 使用 style 控制外框樣式
            )
            CanvasWidth = size.width
            CanvasHeight = size.height

            // 繪製紅色的線， 從 (200,800) 畫到右下角, 線條粗細 10
            // size.width：畫布的總寬度（Float）
            // size.height：畫布的總高度（Float)
            drawLine(
                Color.Red,
                Offset(200f, 800f),
                Offset(size.width, size.height),
                strokeWidth = 10f
            )



            // 繪製綠色路徑，透明度1 (不透明)，線條粗細10
            drawPath(path, Color.Green, 1f, Stroke(width = 10f))

            //取得小小兵圖檔的寬度與高度

            val minionsWidth = minionsImage.width
            val minionsHeight = minionsImage.height

            // 置中公式：(畫布寬高 - 圖片寬高) / 2f
            val offsetX = (size.width - minionsWidth) / 2f
            val offsetY = (size.height - minionsHeight) / 2f

            // 將畫布起點移至置中位置後畫圖
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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CanvasTheme {
        Greeting("Android")
    }
}