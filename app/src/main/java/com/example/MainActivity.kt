package com.example

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.KittyAppEntity
import com.example.ui.theme.MyApplicationTheme
import java.io.File
import kotlinx.coroutines.delay

// Premium Hacker-Slate color palette
val BackgroundBlack = Color(0xFF090A0F)
val CardSlate = Color(0xFF131622)
val TerminalDark = Color(0xFF08090C)
val AccentPink = Color(0xFFFF4081)
val TerminalGreen = Color(0xFF00E676)
val BoundaryGray = Color(0xFF262C40)
val TextLight = Color(0xFFF1F5F9)
val TextMuted = Color(0xFF94A3B8)

enum class Screen {
    KITTYSPACE,
    KITTYDUMPER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundBlack
                ) {
                    KittyDumperMainScreen()
                }
            }
        }
    }
}

@Composable
fun GlowPawPrint(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val neonGreen = Color(0xFF00E676)
        
        // Central cushion pad
        val padPath = Path().apply {
            moveTo(w * 0.5f, h * 0.45f)
            cubicTo(w * 0.22f, h * 0.45f, w * 0.18f, h * 0.76f, w * 0.32f, h * 0.86f)
            cubicTo(w * 0.40f, h * 0.92f, w * 0.60f, h * 0.92f, w * 0.68f, h * 0.86f)
            cubicTo(w * 0.82f, h * 0.76f, w * 0.78f, h * 0.45f, w * 0.5f, h * 0.45f)
            close()
        }
        drawPath(padPath, color = neonGreen)
        
        // 4 Toe pads
        // Outer Left
        drawOval(
            color = neonGreen,
            topLeft = Offset(w * 0.10f, h * 0.32f),
            size = Size(w * 0.15f, h * 0.22f)
        )
        // Inner Left
        drawOval(
            color = neonGreen,
            topLeft = Offset(w * 0.30f, h * 0.14f),
            size = Size(w * 0.17f, h * 0.26f)
        )
        // Inner Right
        drawOval(
            color = neonGreen,
            topLeft = Offset(w * 0.53f, h * 0.14f),
            size = Size(w * 0.17f, h * 0.26f)
        )
        // Outer Right
        drawOval(
            color = neonGreen,
            topLeft = Offset(w * 0.75f, h * 0.32f),
            size = Size(w * 0.15f, h * 0.22f)
        )
        
        // Claws
        val claw1 = Path().apply {
            moveTo(w * 0.38f, h * 0.12f)
            lineTo(w * 0.38f, h * 0.04f)
            lineTo(w * 0.40f, h * 0.12f)
            close()
        }
        drawPath(claw1, color = neonGreen)
        
        val claw2 = Path().apply {
            moveTo(w * 0.62f, h * 0.12f)
            lineTo(w * 0.62f, h * 0.04f)
            lineTo(w * 0.60f, h * 0.12f)
            close()
        }
        drawPath(claw2, color = neonGreen)
    }
}

@Composable
fun KittyspyAngledHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Neon Glowing Paw icon
        GlowPawPrint(
            modifier = Modifier
                .size(72.dp)
                .padding(bottom = 2.dp)
        )
        
        // "KITTYSPY" title with white angled bordering container lines
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                val path = Path().apply {
                    // Start left
                    moveTo(w * 0.04f, h * 0.1f)
                    // Slope down to flat bottom
                    lineTo(w * 0.21f, h * 0.85f)
                    // Flat baseline segment right
                    lineTo(w * 0.79f, h * 0.85f)
                    // Slope up to top right
                    lineTo(w * 0.96f, h * 0.1f)
                }
                
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            
            Text(
                text = "KITTYSPY_OS",
                color = Color(0xFF00E676),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        // "KITTYSPACE" text and thick neon separator
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "> KITTY_SPACE <",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(Color(0xFF00E676))
            )
        }
    }
}

@Composable
fun GlassyContainer(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // Keep it a responsive square
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1730).copy(alpha = 0.45f)) // semi-transparent deep violet slate
            .border(
                border = BorderStroke(
                    1.5.dp,
                    if (isSelected) Color(0xFF00E676) else Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .drawBehind {
                // Glassy reflection glare path at top-left
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    cubicTo(size.width * 0.9f, size.height * 0.45f, size.width * 0.1f, size.height * 0.45f, 0f, 0f)
                    close()
                }
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KittyDumperMainScreen(viewModel: KittyViewModel = viewModel()) {
    val context = LocalContext.current
    
    // Notification permission request for Android 13+
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { _ -> }
        LaunchedEffect(Unit) {
            val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    var activeScreen by remember { mutableStateOf(Screen.KITTYSPACE) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Unity Games, 1 = Unreal Games
    var showAddAppDialog by remember { mutableStateOf(false) }
    var selectedSpaceApp by remember { mutableStateOf<KittyAppEntity?>(null) }
    var launchingApp by remember { mutableStateOf<KittyAppEntity?>(null) }
    var launchLogs by remember { mutableStateOf("") }
    
    // View state mappings
    val kittySpaceApps by viewModel.kittySpaceApps.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val isScanningApps by viewModel.isScanningApps.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val isDumping by viewModel.isDumping.collectAsState()
    val dumpSuccessFile by viewModel.dumpSuccessFile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Pickers for direct files storage mapping
    // Unity storage URIs
    var selectedUnityMetadataUri by remember { mutableStateOf<Uri?>(null) }
    var selectedUnityMetadataName by remember { mutableStateOf<String?>(null) }
    var selectedUnityMetadataSize by remember { mutableStateOf<Long?>(null) }

    var selectedUnityLibUri by remember { mutableStateOf<Uri?>(null) }
    var selectedUnityLibName by remember { mutableStateOf<String?>(null) }
    var selectedUnityLibSize by remember { mutableStateOf<Long?>(null) }

    // Unreal storage URIs
    var selectedUnrealLibUri by remember { mutableStateOf<Uri?>(null) }
    var selectedUnrealLibName by remember { mutableStateOf<String?>(null) }
    var selectedUnrealLibSize by remember { mutableStateOf<Long?>(null) }

    val pickMetadataLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            selectedUnityMetadataUri = uri
            val details = getUriFileNameAndSize(context, uri)
            selectedUnityMetadataName = details.first
            selectedUnityMetadataSize = details.second
        }
    }

    val pickUnityLibLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            selectedUnityLibUri = uri
            val details = getUriFileNameAndSize(context, uri)
            selectedUnityLibName = details.first
            selectedUnityLibSize = details.second
        }
    }

    val pickUnrealLibLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            selectedUnrealLibUri = uri
            val details = getUriFileNameAndSize(context, uri)
            selectedUnrealLibName = details.first
            selectedUnrealLibSize = details.second
        }
    }

    // Launch environment hook simulator coroutine trigger
    LaunchedEffect(launchingApp) {
        if (launchingApp == null) {
            launchLogs = ""
            return@LaunchedEffect
        }
        val app = launchingApp!!
        val pName = app.packageName
        val aName = app.appName

        launchLogs = "[Sandbox] Accessing Virtual Sandbox memory layout...\n"
        delay(400)
        launchLogs += "[Platform] Spawning isolator PID: ${1000 + (1000..9000).random()} in separate space\n"
        delay(400)
        
        // JNI Initialize virtual environment logs
        val launchRet = NativeDumper.initializeVirtualLaunch(pName, aName)
        launchLogs += "$launchRet\n"
        delay(600)
        
        launchLogs += "[KittyMemory] Constructing relocatable virtual maps...\n"
        delay(300)
        
        // JNI Patch simulation call
        val patchRet = NativeDumper.patchMemorySimulation(pName, 0x4E20FL, "FD 03 1F D6")
        launchLogs += "$patchRet\n"
        delay(600)
        
        launchLogs += "[Dobby] Resolving export symbols to load detour logic lines...\n"
        delay(350)
        
        // JNI Hook simulation call
        val dobbyRet = NativeDumper.dobyInlineHookSimulation(pName, "il2cpp_class_from_name", 0x3AA20L)
        launchLogs += "$dobbyRet\n"
        delay(600)
        
        launchLogs += "\n[Sandbox] SUCCESS: App loaded, KittyMemory & Dobby inject setup active.\n"
        launchLogs += "[Shell] Redirecting thread controllers and graphics context...\n"
        launchLogs += "[System] Invoking hardware Intent triggers..."
        delay(800)

        // Launch the actual game on the phone!
        try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(pName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Virtual Simulation Completed. App is not physically launchable on device.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error starting: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        launchingApp = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            
            if (activeScreen == Screen.KITTYSPACE) {
                // REDESIGNED PORTFOLIO HEADER (AS SHOWN IN CAPTURE IMAGE)
                KittyspyAngledHeader()
                
                Spacer(modifier = Modifier.height(18.dp))
                
                // GLASSY CONTAINER GRID (Only "+" add button and actual games added)
                val gridComposables = mutableListOf<@Composable () -> Unit>()
                
                // Bold "+" button inside glassy morphic box
                gridComposables.add {
                    GlassyContainer(
                        onClick = { showAddAppDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add game button",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
                
                // Process added apps mapping them into exact same glassy style
                kittySpaceApps.forEach { app ->
                    val appIcon = rememberAppIcon(context, app.packageName)
                    val isSelected = selectedSpaceApp?.packageName == app.packageName
                    
                    gridComposables.add {
                        GlassyContainer(
                            isSelected = isSelected,
                            onClick = {
                                selectedSpaceApp = if (isSelected) null else app
                                // Launch inside Virtual sandbox environment overlay
                                launchingApp = app
                            }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                Box(modifier = Modifier.size(46.dp)) {
                                    if (appIcon != null) {
                                        Image(
                                            bitmap = appIcon,
                                            contentDescription = "Icon",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(BoundaryGray, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = TextMuted,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = app.appName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF00E676),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "pkg: " + app.packageName,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                            
                            // Delete button
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.8f))
                                    .clickable {
                                        viewModel.removeAppFromKittySpace(app.packageName)
                                        if (isSelected) selectedSpaceApp = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Delete from space",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
                
                // Display composables as a robust 3-column scrollable layout grid
                val rows = gridComposables.chunked(3)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    items(rows) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { item ->
                                Box(modifier = Modifier.weight(1f)) {
                                    item()
                                }
                            }
                            if (rowItems.size < 3) {
                                repeat(3 - rowItems.size) {
                                    Box(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

            } else {
                // --- KITTY DUMPER TAB CONTENT ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kitty_dumper_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "KITTY_DUMPER_CONSOLE",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        fontSize = 17.sp,
                        color = AccentPink
                    )
                }

                // Unity / Unreal Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BackgroundBlack,
                    contentColor = AccentPink,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AccentPink
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                        },
                        text = {
                            Text(
                                text = "UNITY_ENGINE",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (selectedTab == 0) AccentPink else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                        },
                        text = {
                            Text(
                                text = "UNREAL_ENGINE",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (selectedTab == 1) AccentPink else TextMuted
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions Workspace
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f),
                    colors = CardDefaults.cardColors(containerColor = CardSlate),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BoundaryGray)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (selectedTab == 0) "[SYS]::UNITY_DUMPER_ENV" else "[SYS]::UNREAL_DUMPER_ENV",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = AccentPink,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedTab == 0) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "[MODE_A]::EXTRAPOLATE_ACTIVE_SPACE_APP",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                if (selectedSpaceApp != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BackgroundBlack, RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = TerminalGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(selectedSpaceApp!!.appName.uppercase(), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = TextLight, fontSize = 12.sp)
                                            Text("pkg: ${selectedSpaceApp!!.packageName}", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Button(
                                            onClick = { viewModel.dumpUnityFromKittySpace(selectedSpaceApp!!) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            enabled = !isDumping
                                        ) {
                                            if (isDumping) {
                                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White)
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("RUN_DUMP", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BackgroundBlack, RoundedCornerShape(8.dp))
                                            .border(1.dp, BoundaryGray, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("[SYS] Awaiting target payload binding in KittySpace", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    "[MODE_B]::MANUAL_BINARY_LOADER_FILES",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // global-metadata target
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("global-metadata.dat file", fontSize = 10.sp, color = TextMuted)
                                        Text(
                                            text = selectedUnityMetadataName ?: "Not selected (Locate file)",
                                            color = if (selectedUnityMetadataUri != null) TextLight else TextMuted,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { pickMetadataLauncher.launch("application/octet-stream") },
                                        border = BorderStroke(1.dp, if (selectedUnityMetadataUri != null) TerminalGreen else AccentPink),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (selectedUnityMetadataUri != null) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = if (selectedUnityMetadataUri != null) TerminalGreen else AccentPink
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(if (selectedUnityMetadataUri != null) "LOADED" else "LOAD", fontSize = 10.sp)
                                    }
                                }

                                // libil2cpp target
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("libil2cpp.so binary", fontSize = 10.sp, color = TextMuted)
                                        Text(
                                            text = selectedUnityLibName ?: "Not selected (Locate binary)",
                                            color = if (selectedUnityLibUri != null) TextLight else TextMuted,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { pickUnityLibLauncher.launch("application/octet-stream") },
                                        border = BorderStroke(1.dp, if (selectedUnityLibUri != null) TerminalGreen else AccentPink),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (selectedUnityLibUri != null) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = if (selectedUnityLibUri != null) TerminalGreen else AccentPink
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(if (selectedUnityLibUri != null) "LOADED" else "LOAD", fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Button(
                                    onClick = {
                                        val metaUri = selectedUnityMetadataUri
                                        val libUri = selectedUnityLibUri
                                        if (metaUri != null && libUri != null) {
                                            try {
                                                val mStr = context.contentResolver.openInputStream(metaUri)
                                                val lStr = context.contentResolver.openInputStream(libUri)
                                                if (mStr != null && lStr != null) {
                                                    viewModel.dumpUnityFromStorage(mStr, lStr)
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = (selectedUnityLibUri != null && selectedUnityMetadataUri != null && !isDumping)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("NATIVE EXTRACT CONSTRUCT DUMP", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp, fontSize = 12.sp)
                                }
                            }
                        } else {
                            // UNREAL SECTION
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "[MODE_A]::EXTRAPOLATE_ACTIVE_SPACE_APP",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                if (selectedSpaceApp != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BackgroundBlack, RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = TerminalGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(selectedSpaceApp!!.appName.uppercase(), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = TextLight, fontSize = 12.sp)
                                            Text("pkg: ${selectedSpaceApp!!.packageName}", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Button(
                                            onClick = { viewModel.dumpUnrealFromKittySpace(selectedSpaceApp!!) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            enabled = !isDumping
                                        ) {
                                            if (isDumping) {
                                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White)
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("RUN_DUMP", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BackgroundBlack, RoundedCornerShape(8.dp))
                                            .border(1.dp, BoundaryGray, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("[SYS] Awaiting target payload binding in KittySpace", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    "[MODE_B]::MANUAL_BINARY_LOADER_FILES",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("libUE4.so binary ELF", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
                                        Text(
                                            text = selectedUnrealLibName ?: "Not selected (Locate unreal lib)",
                                            color = if (selectedUnrealLibUri != null) TextLight else TextMuted,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { pickUnrealLibLauncher.launch("application/octet-stream") },
                                        border = BorderStroke(1.dp, if (selectedUnrealLibUri != null) TerminalGreen else AccentPink),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (selectedUnrealLibUri != null) Icons.Default.Check else Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = if (selectedUnrealLibUri != null) TerminalGreen else AccentPink
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(if (selectedUnrealLibUri != null) "LOADED" else "LOAD", fontSize = 10.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Button(
                                    onClick = {
                                        val libUri = selectedUnrealLibUri
                                        if (libUri != null) {
                                            try {
                                                val lStr = context.contentResolver.openInputStream(libUri)
                                                if (lStr != null) {
                                                    viewModel.dumpUnrealFromStorage(lStr)
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = (selectedUnrealLibUri != null && !isDumping)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RUN NATIVE CORE SYMBOL EXTRACTOR", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // MONOSPACE TERMINAL LOGGER CONSOLE SECTION
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = TerminalDark),
                    border = BorderStroke(1.dp, BoundaryGray),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isDumping) AccentPink else TerminalGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Terminal Logs Console",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalGreen
                                )
                            }

                            Row {
                                if (dumpSuccessFile != null) {
                                    IconButton(
                                        onClick = { shareDumpFileContents(context, dumpSuccessFile!!) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = AccentPink, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { copyDumpResultToClipboard(context, dumpSuccessFile!!) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Copy", tint = AccentPink, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.clearLogs() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    if (logs.isEmpty()) {
                                        Text(
                                            text = "kitty_dumper@hacker_space:~$ _\n" +
                                                   "[Idle] Waiting for dumper execution instructions...\n" +
                                                   "[Info] File storage directory: Documents/KittyDumper/\n" +
                                                   "[JNI] JNI Loaded mapping helpers verified.",
                                            color = TextMuted,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    } else {
                                        Text(
                                            text = logs,
                                            color = TerminalGreen,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTTOM NAVIGATION BAR (MATCHES EXACTLY THE PROTO IMAGE DESIGN)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // KITTYSPACE Tab Button
                Button(
                    onClick = { activeScreen = Screen.KITTYSPACE },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeScreen == Screen.KITTYSPACE) Color(0xFF00E676) else Color(0xFF13131B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.2.dp, if (activeScreen == Screen.KITTYSPACE) Color(0xFF00E676) else Color.White.copy(alpha = 0.12f))
                ) {
                    Text(
                        text = "KITTYSPACE",
                        color = if (activeScreen == Screen.KITTYSPACE) Color.Black else Color(0xFF00E676),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                }
                
                // KITTYDUMPER Tab Button
                Button(
                    onClick = { activeScreen = Screen.KITTYDUMPER },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeScreen == Screen.KITTYDUMPER) Color(0xFF00E676) else Color(0xFF13131B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.2.dp, if (activeScreen == Screen.KITTYDUMPER) Color(0xFF00E676) else Color.White.copy(alpha = 0.12f))
                ) {
                    Text(
                        text = "KITTYDUMPER",
                        color = if (activeScreen == Screen.KITTYDUMPER) Color.Black else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // MULTI-BOOTLOADER HOOK DETOUR CONSOLE LOG DIALOG (LAUNCH SIMULATOR DETECTOR)
    if (launchingApp != null) {
        Dialog(onDismissRequest = { launchingApp = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.68f),
                colors = CardDefaults.cardColors(containerColor = TerminalDark),
                border = BorderStroke(2.dp, Color(0xFF00E676)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlowPawPrint(modifier = Modifier.size(34.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "VIRTUAL CONTAINER RUNTIME",
                            color = Color(0xFF00E676),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Package: ${launchingApp?.packageName}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 44.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Console Loader Log Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .border(1.dp, BoundaryGray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Text(
                                    text = launchLogs,
                                    color = Color(0xFF00E676),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF00E676),
                            strokeWidth = 2.dp
                        )
                        Text(
                            "DETOUR INJECTOR ACTIVE",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Button(
                            onClick = { launchingApp = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("ABORT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // MAIN SELECTION INTERPRETER FOR ADDING APP DIALOG
    if (showAddAppDialog) {
        Dialog(onDismissRequest = { showAddAppDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AccentPink)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Add Apps to Kitty Space",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AccentPink
                    )
                    Text(
                        "Search and select applications/games on the device to add them directly to tracking space.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var searchQuery by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search installed application labels...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = AccentPink) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentPink,
                            unfocusedBorderColor = BoundaryGray,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isScanningApps) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = AccentPink)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Reading platform packages catalog...", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    } else {
                        val filteredList = installedApps.filter {
                            it.appName.contains(searchQuery, ignoreCase = true) ||
                            it.packageName.contains(searchQuery, ignoreCase = true)
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredList) { app ->
                                val inSpace = kittySpaceApps.any { it.packageName == app.packageName }
                                val iconBitmap = rememberAppIcon(context, app.packageName)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (inSpace) Color.White.copy(alpha = 0.03f) else BackgroundBlack,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (inSpace) AccentPink.copy(alpha = 0.3f) else BoundaryGray,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = !inSpace) {
                                            viewModel.addAppToKittySpace(app)
                                            showAddAppDialog = false
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (iconBitmap != null) {
                                        Image(
                                            bitmap = iconBitmap,
                                            contentDescription = "App Icon",
                                            modifier = Modifier.size(36.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(BoundaryGray, RoundedCornerShape(6.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextMuted)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            app.appName,
                                            fontWeight = FontWeight.Bold,
                                            color = TextLight,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            app.packageName,
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (inSpace) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Added",
                                            tint = TerminalGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showAddAppDialog = false },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CLOSE")
                    }
                }
            }
        }
    }
}

// Converts generic Android Drawable into standard Jetpack Compose compatible ImageBitmap
fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap.asImageBitmap()
    }
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(1),
        drawable.intrinsicHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val canvas = AndroidCanvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

@Composable
fun rememberAppIcon(context: Context, packageName: String): ImageBitmap? {
    return remember(packageName) {
        try {
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(packageName)
            drawableToImageBitmap(drawable)
        } catch (e: Exception) {
            null
        }
    }
}

// SAF URI inspector to retrieve descriptive metadata info
fun getUriFileNameAndSize(context: Context, uri: Uri): Pair<String, Long> {
    var name = "unknown_file"
    var size = 0L
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex != -1) name = cursor.getString(nameIndex)
                if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
            }
        }
    } catch (e: Exception) {
        name = uri.lastPathSegment ?: "binary_data.dat"
    }
    return Pair(name, size)
}

// Terminal Clipboard share controller
fun copyDumpResultToClipboard(context: Context, file: File) {
    try {
        if (!file.exists()) return
        val text = file.readText()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Kitty Dumper Output", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied dump outputs to clipboard successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to copy output: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// System text sharing dispatcher
fun shareDumpFileContents(context: Context, file: File) {
    try {
        if (!file.exists()) return
        val text = file.readText()
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "Kitty Dumper Export - ${file.name}")
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Dump Assets Class")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to initialize standard share provider: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
