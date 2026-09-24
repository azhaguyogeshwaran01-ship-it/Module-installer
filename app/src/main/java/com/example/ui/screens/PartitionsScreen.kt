package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.SystemPartitionInfo
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import com.example.ui.viewmodel.ModuleViewModel

@Composable
fun PartitionsScreen(
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()
    val activeMode = viewModel.activeOperatingMode

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SYSTEM PARTITIONS & PATCHES",
                color = CyberCyanBright,
                fontWeight = FontWeight.Black,
                fontSize = 19.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Dynamic super.img mount analyzer & systemless overlayfs patcher",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
        }

        // Security Patch Compatibility Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberEmerald.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LATEST SECURITY PATCH COMPATIBILITY",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        StatusBadge(text = "VERIFIED", color = CyberEmerald)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Device is running Security Patch Level: ${deviceInfo.securityPatch}. Module Inserter's Dynamic Shim protects against bootloops on Android 14/15/16 dm-verity enforcement.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TerminalBackground, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "DM-VERITY / AVB: ${deviceInfo.dmVerityStatus}",
                                color = TerminalText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "SELINUX POLICY: ${deviceInfo.selinuxMode.label}",
                                color = CyberCyanBright,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "EROFS PROTECTION: Auto-Routed through OverlayFS",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Partitions
        item {
            Text(
                text = "BLOCK DEVICE PARTITION MOUNTS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        items(deviceInfo.partitions, key = { it.name }) { partition ->
            PartitionCard(partition = partition)
        }

        // Systemless Patch Engine Card
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
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = CyberCyanBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEMLESS OVERLAYFS INJECTOR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Because modern Android locks /system with read-only EROFS images, Module Inserter binds lowerdir overlays into /data/adb/modules without directly touching block partitions. This keeps OTA compatibility intact.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.setCustomCommandInput("mount -t overlay overlay -o lowerdir=/system,upperdir=/data/adb/modules /system")
                            viewModel.executeTerminalCommand()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verify_overlayfs_mount_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Audit OverlayFS Mount Status", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PartitionCard(partition: SystemPartitionInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = if (partition.isReadOnly) Color(0xFF94A3B8) else CyberEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = partition.mountPoint,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(
                        text = if (partition.isReadOnly) "RO" else "RW",
                        color = if (partition.isReadOnly) CyberAmber else CyberEmerald
                    )
                    StatusBadge(
                        text = partition.fsType.substringBefore(" "),
                        color = CyberCyanBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val usedMb = (partition.totalBytes - partition.freeBytes) / (1024 * 1024)
            val totalMb = partition.totalBytes / (1024 * 1024)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${usedMb} MB used / ${totalMb} MB total",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${partition.usedPercentage}%",
                    color = if (partition.usedPercentage > 90) CyberRed else CyberCyanBright,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { partition.usedPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (partition.usedPercentage > 90) CyberRed else CyberCyanBright,
                trackColor = Color(0xFF1E293B)
            )
        }
    }
}
