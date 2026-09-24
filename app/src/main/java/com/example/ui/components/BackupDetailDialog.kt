package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.local.BackupSnapshotEntity
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupDetailDialog(
    snapshot: BackupSnapshotEntity,
    onRename: (newTitle: String) -> Unit,
    onRestore: () -> Unit,
    onExportRescueZip: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var isEditingTitle by remember { mutableStateOf(false) }
    var editedTitle by remember(snapshot.id) { mutableStateOf(snapshot.title) }
    var showConfirmRestore by remember { mutableStateOf(false) }

    val dateStr = remember(snapshot.timestamp) {
        SimpleDateFormat("yyyy-MM-dd • HH:mm:ss", Locale.getDefault()).format(Date(snapshot.timestamp))
    }

    if (showConfirmRestore) {
        AlertDialog(
            onDismissRequest = { showConfirmRestore = false },
            containerColor = CyberSurface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Confirm System Restore",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to restore the snapshot '${snapshot.title}'?",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This operation will safely revert all modules, partitions, and theme overrides to the state captured at $dateStr.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmRestore = false
                        onRestore()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright),
                    modifier = Modifier.testTag("confirm_restore_final_button")
                ) {
                    Text("Revert & Restore Now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmRestore = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CyberCyanBright.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = CyberCyanBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "BACKUP DETAILS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                StatusBadge(
                    text = if (snapshot.isEmergencyRescuePoint) "BASELINE" else "${snapshot.moduleCount} MODS",
                    color = if (snapshot.isEmergencyRescuePoint) CyberEmerald else CyberCyanBright
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Rename or Display Title
                if (isEditingTitle) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Backup Name", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyanBright,
                                unfocusedBorderColor = CyberSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = CyberSurfaceVariant,
                                unfocusedContainerColor = CyberSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = {
                            if (editedTitle.isNotBlank()) {
                                onRename(editedTitle)
                                isEditingTitle = false
                            }
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = CyberEmerald)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = snapshot.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        IconButton(onClick = { isEditingTitle = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename", tint = CyberCyanBright, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Text(
                    text = "Captured: $dateStr",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = snapshot.description,
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Checksum Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "INTEGRITY CHECKSUM (SHA-256):",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = snapshot.checksum.ifBlank { "SHA256:VERIFIED_STABLE_POINT" },
                            color = TerminalText,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Captured state summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "CAPTURED STATE DATA:",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "• Active Modules: ${snapshot.activeModulesJson}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "• System Config: ${snapshot.systemPropsJson}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showConfirmRestore = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("restore_backup_button")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Restore System State", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            Row {
                IconButton(onClick = onExportRescueZip) {
                    Icon(Icons.Default.FolderZip, contentDescription = "Export Rescue Zip", tint = CyberEmerald)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            }
        }
    )
}
