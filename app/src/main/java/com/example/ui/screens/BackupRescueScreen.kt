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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.BackupSnapshotEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import com.example.ui.viewmodel.ModuleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRescueScreen(
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val snapshots by viewModel.snapshots.collectAsStateWithLifecycle()
    var showSnapshotDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "STABILITY & BACKUP GUARD",
                color = CyberCyanBright,
                fontWeight = FontWeight.Black,
                fontSize = 19.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Pre-modification snapshots, bootloop prevention, and 1-tap state restoration",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp
            )
        }

        // Stability Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, CyberEmerald.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUTOMATIC WATCHDOG & RESTORE",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        StatusBadge(text = "ARMED", color = CyberEmerald)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Module Inserter prompts you to create a full system snapshot before any modification is applied. If any issue occurs, restore that snapshot with 1 tap to return the device to its exact prior state.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generateRescueZip() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("compile_rescue_zip_button")
                        ) {
                            Icon(Icons.Default.FolderZip, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Flashable ZIP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.triggerEmergencyRollback() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("emergency_rollback_button")
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Full Neutralize", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section Title: Manual Snapshot action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STORED SYSTEM BACKUPS (${snapshots.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.Monospace
                )

                OutlinedButton(
                    onClick = { showSnapshotDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyanBright),
                    modifier = Modifier.testTag("create_snapshot_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Backup", fontSize = 12.sp)
                }
            }
        }

        items(snapshots, key = { it.id }) { snapshot ->
            SnapshotCard(
                snapshot = snapshot,
                onClick = { viewModel.openBackupDetail(snapshot) },
                onDelete = { viewModel.deleteSnapshot(snapshot) },
                onRestore = { viewModel.restoreSnapshot(snapshot) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSnapshotDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSnapshotDialog = false },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Create Full System Backup", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Creates a stability checkpoint of all currently mounted modules, build properties, and theme overlays.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Backup Label", color = Color(0xFF94A3B8)) },
                        placeholder = { Text("e.g. Clean_Baseline_Setup") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyanBright,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notes / Description", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyanBright,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createManualSnapshot(title, description)
                        showSnapshotDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright)
                ) {
                    Text("Save Backup", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSnapshotDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
fun SnapshotCard(
    snapshot: BackupSnapshotEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRestore: () -> Unit
) {
    val dateStr = remember(snapshot.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm:ss", Locale.getDefault())
        sdf.format(Date(snapshot.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("snapshot_card_${snapshot.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyberCyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = snapshot.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                StatusBadge(
                    text = if (snapshot.isEmergencyRescuePoint) "BASELINE" else "${snapshot.moduleCount} MODS",
                    color = if (snapshot.isEmergencyRescuePoint) CyberEmerald else CyberCyanBright
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateStr,
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = snapshot.description,
                color = Color(0xFFE2E8F0),
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TerminalBackground, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "CHECKSUM: ${snapshot.checksum.take(34)}",
                    color = TerminalText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onClick,
                    modifier = Modifier.testTag("view_details_${snapshot.id}")
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyanBright, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View / Rename", color = CyberCyanBright, fontSize = 11.5.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onRestore,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("restore_snapshot_btn_${snapshot.id}")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Snapshot",
                            tint = CyberRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
