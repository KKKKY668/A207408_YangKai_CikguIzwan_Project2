package com.example.a207408_cikguizwan_lab02

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.a207408_cikguizwan_lab02.ui.theme.WildLensTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentifyScreen(viewModel: WildLensViewModel, onBack: () -> Unit) {
    var identified by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }

    // 用于页面动态展示随机出来的识别结果
    var currentSpecies by remember { mutableStateOf("Malayan Tiger") }
    var currentImage by remember { mutableIntStateOf(R.drawable.img_tiger) }

    val coroutineScope = rememberCoroutineScope()

    WildLensTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Identify Species", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Camera viewfinder mockup
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isScanning) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Scanning...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                        } else if (identified) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("✅", style = MaterialTheme.typography.displaySmall)
                                // 动态显示随机出来的名字
                                Text(
                                    text = currentSpecies,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Identified via WildLens AI",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Protected Species", // 稍微泛化一点的标签，适应所有动物
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_camera),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Point camera at a plant or animal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Shutter button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        // 扫描时按钮底色变灰，提示用户无法点击
                        //before  .background(if (isScanning) Color.Gray else MaterialTheme.colorScheme.primary, CircleShape),
                        .background(if (isScanning) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            identified = false
                            isScanning = true

                            coroutineScope.launch {
                                delay(1500) // 模拟 1.5 秒的扫描延迟

                                // 题库，每次扫描随机出一个动物
                                val randomData = listOf(
                                    Pair("Malayan Tiger", R.drawable.img_tiger),
                                    Pair("Hornbill", R.drawable.img_hornbill),
                                    Pair("Rafflesia", R.drawable.img_rafflesia),
                                    Pair("Sun Bear", R.drawable.img_bear)
                                ).random()

                                // 更新 UI 显示状态
                                currentSpecies = randomData.first
                                currentImage = randomData.second

                                isScanning = false
                                identified = true

                                // 核心逻辑：存入 ViewModel 以供 ActivityScreen 显示
                                val currentLocation = viewModel.userProfile.value.location.ifBlank { "Nearby" }
                                viewModel.addActivityLog(
                                    ActivityLog(
                                        species = currentSpecies,
                                        location = currentLocation,
                                        time = "Just now",
                                        imageRes = currentImage
                                    )
                                )
                            }
                        },
                        // 防狂点设计，扫描中直接禁用按钮
                        enabled = !isScanning,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Identify",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                if (identified) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("SDG 15 Impact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "This species is protected under Malaysia's Wildlife Conservation Act. Spotting one contributes to biodiversity tracking data. 🌿",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // 成功添加的交互提示
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✔ Successfully added to Activity Log",
                        style = MaterialTheme.typography.labelMedium,
                        // before  color = Color(0xFF4CAF50),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!identified && !isScanning) {
                    Text(
                        text = "Tap the button to identify a nearby species",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}