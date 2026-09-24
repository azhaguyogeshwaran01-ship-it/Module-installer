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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CompatibilityStatus
import com.example.model.DeviceSystemInfo
import com.example.model.ModuleCompatibilityReport
import com.example.model.SecurityPatchAudit
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText

@Composable
fun CompatibilityCheckDialog(
    report: ModuleCompatibilityReport?,
    patchAudit: SecurityPatchAudit,
    deviceInfo: DeviceSystemInfo,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(CyberCyanBright.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberCyanBright,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (report != null) "MODULE COMPATIBILITY AUDIT" else "OS & SECURITY PATCH SCANNER",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Android ${deviceInfo.androidVersion} • Patch ${deviceInfo.securityPatch}",
                        color = CyberCyanBright,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section 1: Specific Module Report if provided
                if (report != null) {
                    val statusColor = when (report.status) {
                        CompatibilityStatus.FULLY_COMPATIBLE -> CyberEmerald
                        CompatibilityStatus.COMPATIBLE_WITH_SHIM -> CyberCyanBright
                        CompatibilityStatus.SECURITY_PATCH_WARNING -> CyberAmber
                        CompatibilityStatus.INCOMPATIBLE -> CyberRed
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = report.moduleTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                StatusBadge(text = report.status.badgeText, color = statusColor)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = report.headline,
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (report.appliedShim != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(TerminalBackground, RoundedCornerShape(6.dp))
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = "Dynamic Shim: ${report.appliedShim}",
                                        color = CyberCyanBright,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            report.advisories.forEach { adv ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "• ", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    Text(text = adv, color = Color(0xFFCBD5E1), fontSize = 11.5.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Section 2: Security Patch Level & Android Architecture Scanner
                Text(
                    text = "DEVICE OS SECURITY PROFILE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        AuditRow("Security Tier", patchAudit.patchTier, CyberEmerald)
                        AuditRow("16KB Page Kernel", if (patchAudit.supports16KbPages) "Supported (Android 15+)" else "Standard 4KB", CyberCyanBright)
                        AuditRow("dm-verity / AVB", if (patchAudit.enforcesStrictDmVerity) "Enforcing (Locked Super)" else "Permissive", CyberAmber)
                        AuditRow("SELinux Policies", if (patchAudit.requiresSyntheticSepolicy) "Live Sepolicy Ruleset Active" else "Standard", Color(0xFF38BDF8))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ACTIVE DYNAMIC ADAPTERS (${patchAudit.activeShims.size})",
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))

                patchAudit.activeShims.forEach { shim ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = shim,
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dismiss_compatibility_dialog")
            ) {
                Text(text = "Understood", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}

@Composable
fun AuditRow(label: String, value: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = value,
            color = accent,
            fontSize = 11.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold
        )
    }
}
