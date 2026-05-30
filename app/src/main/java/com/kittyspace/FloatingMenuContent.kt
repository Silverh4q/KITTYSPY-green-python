package com.kittyspace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mock memory logs
val MOCK_GAME_LOGS = listOf(
    "--CLASS : WEAPON MANAGER\n| METHOD: SHOOT\n| RVA: 0x25ACF68\n| OFFSET: 0x14F56A0\n|---------------------------",
    "--CLASS : NETWORK CLIENT\n| METHOD: SEND_PACKET\n| RVA: 0x11B340\n| OFFSET: 0x55B340\n|---------------------------",
    "--CLASS : PLAYER_CONTROLLER\n| METHOD: JUMP\n| RVA: 0x77B010\n| OFFSET: 0x66B010\n|---------------------------"
)

@Composable
fun FloatingMenuContent(
    appName: String,
    onCloseMenu: () -> Unit
) {
    var activeTab by remember { mutableStateOf("KITTYSPY") }
    var kittySpyLogs by remember { mutableStateOf(MOCK_GAME_LOGS.joinToString("\n\n")) }

    Card(
        modifier = Modifier
            .width(320.dp)
            .height(400.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090A0F)), // BackgroundBlack
        border = BorderStroke(1.dp, Color(0xFF00E676)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131622)) // CardSlate
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "KITTYSPY",
                    color = Color(0xFFB388FF),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                IconButton(onClick = onCloseMenu, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Menu", tint = Color.Red)
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton("KITTYSPY", activeTab == "KITTYSPY") { activeTab = "KITTYSPY" }
                TabButton("OFFSET PATCH", activeTab == "PATCH") { activeTab = "PATCH" }
                TabButton("HOOKING", activeTab == "HOOKING") { activeTab = "HOOKING" }
            }

            Divider(color = Color(0xFF262C40)) // BoundaryGray

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                when (activeTab) {
                    "KITTYSPY" -> KittySpyTab(appName, kittySpyLogs) { kittySpyLogs = "" }
                    "PATCH" -> OffsetPatchTab()
                    "HOOKING" -> HookingTab()
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (isSelected) Color(0xFF00E676) else Color(0xFF94A3B8), // TerminalGreen or TextMuted
        fontSize = 10.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    )
}

@Composable
fun KittySpyTab(appName: String, logs: String, onClear: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Inspected game: $appName", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF08090C)) // TerminalDark
                .border(1.dp, Color(0xFF262C40)) // BoundaryGray
                .padding(4.dp)
        ) {
            LazyColumn {
                item {
                    Text(
                        text = logs.ifEmpty { "No actions captured yet..." },
                        color = Color(0xFF00E676),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onClear,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262C40)),
                modifier = Modifier.weight(1f).height(32.dp)
            ) {
                Text("CLEAR", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* Save logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB388FF)),
                modifier = Modifier.weight(1f).height(32.dp)
            ) {
                Text("SAVE", color = Color.Black, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun OffsetPatchTab() {
    var offset by remember { mutableStateOf("") }
    var hex by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = offset,
            onValueChange = { offset = it },
            label = { Text("Offset (e.g. 0x12345)", fontSize = 10.sp) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = hex,
            onValueChange = { hex = it },
            label = { Text("Hex Patch (e.g. C0 03 5F D6)", fontSize = 10.sp) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Patch logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                modifier = Modifier.weight(1f)
            ) {
                Text("PATCH", color = Color.Black, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* Restore logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262C40)),
                modifier = Modifier.weight(1f)
            ) {
                Text("RESTORE", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun HookingTab() {
    var methodOffset by remember { mutableStateOf("") }
    var methodName by remember { mutableStateOf("") }
    val fields = remember { mutableStateListOf(Pair("float", "")) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = methodName,
                onValueChange = { methodName = it },
                label = { Text("Method Name", fontSize = 10.sp) },
                modifier = Modifier.weight(1f).height(56.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedTextField(
                value = methodOffset,
                onValueChange = { methodOffset = it },
                label = { Text("Offset", fontSize = 10.sp) },
                modifier = Modifier.weight(1f).height(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Fields:", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(bottom = 4.dp))
        Box(modifier = Modifier.weight(1f).fillMaxWidth().border(1.dp, Color(0xFF262C40)).padding(4.dp)) {
            LazyColumn {
                items(fields.size) { index ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(fields[index].first, color = Color(0xFF94A3B8), fontSize = 10.sp, modifier = Modifier.width(40.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        OutlinedTextField(
                            value = fields[index].second,
                            onValueChange = { newVal -> fields[index] = fields[index].copy(second = newVal) },
                            modifier = Modifier.weight(1f).height(46.dp),
                            singleLine = true
                        )
                    }
                }
                item {
                    Button(
                        onClick = { fields.add(Pair(listOf("int", "float", "bool", "string").random(), "")) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262C40)),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(32.dp)
                    ) {
                        Text("+ Add Field", fontSize = 10.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { /* Hook logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                modifier = Modifier.weight(1f)
            ) {
                Text("HOOK", color = Color.White, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* Unhook logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262C40)),
                modifier = Modifier.weight(1f)
            ) {
                Text("UNHOOK", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}
