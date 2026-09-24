package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.RootStatus
import com.example.ui.components.ModuleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import com.example.ui.viewmodel.ModuleViewModel

@Composable
fun DashboardScreen(
    viewModel: ModuleViewModel,
    onNavigateToModules: () -> Unit,
    onNavigateToTheme: () -> Unit,
    onNavigateToTerminal: () -> Unit,
    onNavigateToBackup: () -> Unit
) {
    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()
    val modules by viewModel.modules.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val activeMode = viewModel.activeOperatingMode
    val activeModules = modules.filter { it.isEnabled }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MODULE INSERTER",
                        color = CyberCyanBright,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Next-Gen System Mod & Tweak Engine",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshDeviceInfo() },
                    modifier = Modifier.testTag("refresh_diagnostics")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh System Diagnostics",
                        tint = CyberCyanBright
                    )
                }
            }
        }

        // Operating Mode Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = CyberCyanBright,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OPERATING ENVIRONMENT",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeChip(
                            label = "Auto-Detect",
                            isSelected = uiState.forcedMode == null,
                            onClick = { viewModel.setForcedMode(null) },
                            modifier = Modifier.weight(1f)
                        )
                        ModeChip(
                            label = "Root (SU)",
                            isSelected = uiState.forcedMode == RootStatus.ROOTED_MAGISK,
                            onClick = { viewModel.setForcedMode(RootStatus.ROOTED_MAGISK) },
                            modifier = Modifier.weight(1f)
                        )
                        ModeChip(
                            label = "Shizuku",
                            isSelected = uiState.forcedMode == RootStatus.NON_ROOTED_SHIZUKU,
                            onClick = { viewModel.setForcedMode(RootStatus.NON_ROOTED_SHIZUKU) },
                            modifier = Modifier.weight(1f)
                        )
                        ModeChip(
                            label = "Sandbox",
                            isSelected = uiState.forcedMode == RootStatus.NON_ROOTED_USERLAND,
                            onClick = { viewModel.setForcedMode(RootStatus.NON_ROOTED_USERLAND) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current Mode: ${activeMode.label}",
                        color = if (activeMode.isElevated) CyberEmerald else CyberCyanBright,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // System Diagnostics Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCyanBright.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(CyberCyanBright.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Android,
                                    contentDescription = null,
                                    tint = CyberCyanBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${deviceInfo.manufacturer} ${deviceInfo.deviceModel}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = deviceInfo.androidVersion,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        StatusBadge(
                            text = if (activeMode.isElevated) "ROOTED" else "NON-ROOT",
                            color = if (activeMode.isElevated) CyberEmerald else CyberCyanBright
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn("Security Patch", deviceInfo.securityPatch, CyberEmerald)
                        InfoColumn("SELinux", deviceInfo.selinuxMode.label.substringBefore(" "), CyberAmber)
                        InfoColumn("Mount Engine", deviceInfo.activeMountMethod.substringBefore(" "), CyberCyanBright)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TerminalBackground, RoundedCornerShape(8.dp))
                            .clickable { viewModel.openSystemPatchAuditDialog() }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = CyberEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Security Patch Armor: Audit Active",
                                    color = TerminalText,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "SCAN →",
                                color = CyberEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        item {
            Text(
                text = "RAPID ACTIONS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    icon = Icons.Default.RestartAlt,
                    title = "Emergency\nRollback",
                    color = CyberRed,
                    onClick = { viewModel.triggerEmergencyRollback() },
                    modifier = Modifier.weight(1f),
                    testTag = "action_emergency_rollback"
                )

                ActionCard(
                    icon = Icons.Default.FolderZip,
                    title = "Compile\nRescue Zip",
                    color = CyberEmerald,
                    onClick = { viewModel.generateRescueZip() },
                    modifier = Modifier.weight(1f),
                    testTag = "action_rescue_zip"
                )

                ActionCard(
                    icon = Icons.Default.Palette,
                    title = "Theme\nOverrides",
                    color = Color(0xFFA855F7),
                    onClick = onNavigateToTheme,
                    modifier = Modifier.weight(1f),
                    testTag = "action_theme_overrides"
                )

                ActionCard(
                    icon = Icons.Default.Terminal,
                    title = "Terminal\nConsole",
                    color = CyberCyanBright,
                    onClick = onNavigateToTerminal,
                    modifier = Modifier.weight(1f),
                    testTag = "action_terminal"
                )
            }
        }

        // Active Injected Modules Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ACTIVE INJECTIONS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(CyberEmerald.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${activeModules.size}",
                            color = CyberEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Manage All →",
                    color = CyberCyanBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToModules() }
                )
            }
        }

        if (activeModules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No modules currently injected",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToModules,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright)
                        ) {
                            Text("Browse Available Modules", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(activeModules.size) { index ->
                val module = activeModules[index]
                val report = viewModel.getModuleCompatibility(module)
                ModuleCard(
                    module = module,
                    onToggle = { viewModel.toggleModule(module) },
                    onInspect = { viewModel.setInspectModule(module) },
                    compatibilityReport = report,
                    onCheckCompatibility = { viewModel.openCompatibilityDialogForModule(module) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoColumn(title: String, value: String, accentColor: Color) {
    Column {
        Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) CyberCyanBright.copy(alpha = 0.2f) else CyberDark,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isSelected) CyberCyanBright else CyberSurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) CyberCyanBright else Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun ActionCard(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
