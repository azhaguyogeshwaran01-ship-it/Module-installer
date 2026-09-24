package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun ModuleCard(
    module: ModuleEntity,
    onToggle: () -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier,
    compatibilityReport: ModuleCompatibilityReport? = null,
    onCheckCompatibility: (() -> Unit)? = null
) {
    val borderColor by animateColorAsState(
        targetValue = if (module.isEnabled) CyberEmerald else CyberSurfaceBorder,
        animationSpec = tween(300),
        label = "card_border"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onInspect() }
            .testTag("module_card_${module.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (module.isEnabled) CyberSurfaceVariant else CyberSurface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status Indicator, Title, Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (module.isEnabled) CyberEmerald else Color(0xFF64748B), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = module.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Switch(
                    checked = module.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberEmerald,
                        checkedTrackColor = CyberEmerald.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("toggle_${module.id}")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Author and Version with high contrast
            Text(
                text = "v${module.version} • by ${module.author}",
                color = Color(0xFFCBD5E1),
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = module.description,
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badges Row & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CategoryBadge(category = module.category)

                    if (compatibilityReport != null) {
                        val compatColor = when (compatibilityReport.status) {
                            CompatibilityStatus.FULLY_COMPATIBLE -> CyberEmerald
                            CompatibilityStatus.COMPATIBLE_WITH_SHIM -> CyberCyanBright
                            CompatibilityStatus.SECURITY_PATCH_WARNING -> CyberAmber
                            CompatibilityStatus.INCOMPATIBLE -> CyberRed
                        }

                        Box(
                            modifier = Modifier
                                .background(compatColor.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                                .border(1.dp, compatColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onCheckCompatibility?.invoke() }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = compatColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = compatibilityReport.status.badgeText,
                                    color = compatColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    } else {
                        if (module.isPartitionPatch) {
                            StatusBadge(text = "OverlayFS", color = Color(0xFF38BDF8))
                        }
                        if (module.isRootRequired) {
                            StatusBadge(text = "Root Only", color = CyberAmber)
                        } else {
                            StatusBadge(text = "Non-Root OK", color = CyberEmerald)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onCheckCompatibility != null) {
                        IconButton(
                            onClick = onCheckCompatibility,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Compatibility Audit",
                                tint = CyberCyanBright,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onInspect,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("inspect_${module.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Inspect Module",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
