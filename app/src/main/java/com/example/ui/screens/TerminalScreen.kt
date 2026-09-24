package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ExecutionLogEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import com.example.ui.viewmodel.ModuleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TerminalScreen(
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeMode = viewModel.activeOperatingMode

    val quickCommands = listOf(
        "getprop ro.build.version.release",
        "getenforce",
        "cmd overlay list",
        "mount | grep overlay",
        "getprop ro.boot.verifiedbootstate"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EXECUTION CONSOLE",
                    color = CyberCyanBright,
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Live kernel hooks, overlayfs mounts, and system audits",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { viewModel.clearTerminalLogs() },
                modifier = Modifier.testTag("clear_logs_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Logs",
                    tint = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Command Shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickCommands.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .background(CyberSurface, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            viewModel.setCustomCommandInput(cmd)
                            viewModel.executeTerminalCommand()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cmd,
                        color = CyberCyanBright,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Command Input Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.customCommandInput,
                onValueChange = { viewModel.setCustomCommandInput(it) },
                placeholder = {
                    Text(
                        if (activeMode.isElevated) "su # Enter command (e.g. mount, getprop)..." else "sh $ Enter shell command...",
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TerminalBackground,
                    unfocusedContainerColor = TerminalBackground,
                    focusedBorderColor = CyberCyanBright,
                    unfocusedBorderColor = CyberSurfaceBorder,
                    focusedTextColor = TerminalText,
                    unfocusedTextColor = TerminalText
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { viewModel.executeTerminalCommand() },
                modifier = Modifier
                    .size(48.dp)
                    .background(CyberCyanBright, RoundedCornerShape(10.dp))
                    .testTag("send_command_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Execute Command",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Terminal Output Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(TerminalBackground, RoundedCornerShape(12.dp))
                .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            if (logs.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Module Inserter Daemon Ready",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Awaiting module injection or shell requests...",
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs, key = { it.id }) { log ->
                        TerminalLogItem(log = log)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TerminalLogItem(log: ExecutionLogEntity) {
    val timeStr = remember(log.timestamp) {
        SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(log.timestamp))
    }

    val tagColor = when (log.tag) {
        "INSERTER" -> CyberCyanBright
        "THEME" -> Color(0xFFA855F7)
        "RESCUE" -> CyberEmerald
        "ROLLBACK" -> CyberRed
        else -> Color(0xFF38BDF8)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "[$timeStr]",
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "[${log.tag}]",
                color = tagColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "> ${log.command}",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (log.output.isNotBlank()) {
            Text(
                text = log.output,
                color = if (log.isSuccess) TerminalText else CyberRed,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
    }
}
