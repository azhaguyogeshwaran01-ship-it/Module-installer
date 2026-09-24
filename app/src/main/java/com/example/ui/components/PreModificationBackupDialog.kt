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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import com.example.data.local.ModuleEntity
import com.example.model.CompatibilityStatus
import com.example.model.ModuleCompatibilityReport
import com.example.ui.theme.CyberAmber
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
fun PreModificationBackupDialog(
    module: ModuleEntity,
    compatibilityReport: ModuleCompatibilityReport,
    onConfirmWithBackup: (backupTitle: String, backupDesc: String) -> Unit,
    onSkipBackup: () -> Unit,
    onDismiss: () -> Unit
) {
    val defaultTitle = remember(module.id) {
        val dateStr = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
        "Backup_Pre_${module.title.replace(" ", "_")}_$dateStr"
    }

    var backupTitle by remember { mutableStateOf(defaultTitle) }
    var backupDesc by remember {
        mutableStateOf("Stability snapshot before enabling ${module.title} (v${module.version}).")
    }
    var includeProps by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberAmber.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "PRE-MODIFICATION BACKUP",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "System Stability Protection",
                        color = CyberAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "A full system snapshot is recommended before applying '${module.title}' to ensure the device can be reverted safely if instability occurs.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Compatibility info card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground, RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            when (compatibilityReport.status) {
                                CompatibilityStatus.FULLY_COMPATIBLE -> CyberEmerald.copy(alpha = 0.5f)
                                CompatibilityStatus.COMPATIBLE_WITH_SHIM -> CyberCyanBright.copy(alpha = 0.5f)
                                CompatibilityStatus.SECURITY_PATCH_WARNING -> CyberAmber.copy(alpha = 0.5f)
                                CompatibilityStatus.INCOMPATIBLE -> CyberRed.copy(alpha = 0.5f)
                            },
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMPATIBILITY CHECK:",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(
                                text = compatibilityReport.status.badgeText,
                                color = when (compatibilityReport.status) {
                                    CompatibilityStatus.FULLY_COMPATIBLE -> CyberEmerald
                                    CompatibilityStatus.COMPATIBLE_WITH_SHIM -> CyberCyanBright
                                    CompatibilityStatus.SECURITY_PATCH_WARNING -> CyberAmber
                                    CompatibilityStatus.INCOMPATIBLE -> CyberRed
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = compatibilityReport.headline,
                            color = TerminalText,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (compatibilityReport.appliedShim != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Active Shim: ${compatibilityReport.appliedShim}",
                                color = CyberCyanBright,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = backupTitle,
                    onValueChange = { backupTitle = it },
                    label = { Text("Backup Snapshot Name", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyanBright,
                        unfocusedBorderColor = CyberSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CyberSurfaceVariant,
                        unfocusedContainerColor = CyberSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("backup_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = backupDesc,
                    onValueChange = { backupDesc = it },
                    label = { Text("Notes / Description", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyanBright,
                        unfocusedBorderColor = CyberSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CyberSurfaceVariant,
                        unfocusedContainerColor = CyberSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = includeProps,
                        onCheckedChange = { includeProps = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CyberEmerald,
                            checkmarkColor = Color.Black
                        )
                    )
                    Text(
                        text = "Capture system properties & overlay states",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmWithBackup(backupTitle, backupDesc) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_backup_apply_button")
            ) {
                Text(
                    text = "Create Backup & Apply",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onSkipBackup,
                    modifier = Modifier.testTag("skip_backup_button")
                ) {
                    Text(
                        text = "Skip Backup",
                        color = CyberAmber,
                        fontSize = 12.sp
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }
    )
}
