package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ThemeOverrideConfig
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.viewmodel.ModuleViewModel

@Composable
fun ThemeOverridesScreen(
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val activeMode = viewModel.activeOperatingMode

    val accentColors = listOf(
        "#0EA5E9" to "Cyber Cyan",
        "#10B981" to "Neon Emerald",
        "#8B5CF6" to "Electric Violet",
        "#F59E0B" to "Sunset Amber",
        "#F43F5E" to "Coral Rose",
        "#E2E8F0" to "Monochrome"
    )

    val iconShapes = listOf("Squircle", "Circle", "Teardrop", "Hexagon", "Pebble")
    val clockPositions = listOf("Left", "Center", "Right")
    val refreshRates = listOf(60, 90, 120, 144)
    val animationScales = listOf(0.25f, 0.5f, 1.0f, 0.0f)

    val activeColorParsed = try {
        Color(android.graphics.Color.parseColor(themeConfig.accentColorHex))
    } catch (_: Exception) {
        CyberCyanBright
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "THEME & OVERLAY ENGINE",
                color = CyberCyanBright,
                fontWeight = FontWeight.Black,
                fontSize = 19.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Fabricated Overlays (RRO) & SystemUI Customizer for Root & Non-Root",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )
        }

        // Interactive Live Preview Device Mockup
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, activeColorParsed.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE SYSTEMUI PREVIEW",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        StatusBadge(
                            text = if (activeMode.isElevated) "SYSTEMLESS RRO" else "SHIZUKU FABRICATED",
                            color = activeColorParsed
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mock Status Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = when (themeConfig.clockPosition) {
                                "Left" -> Arrangement.SpaceBetween
                                "Center" -> Arrangement.SpaceBetween
                                else -> Arrangement.SpaceBetween
                            }
                        ) {
                            if (themeConfig.clockPosition == "Left") {
                                Text(
                                    text = if (themeConfig.showSeconds) "10:42:18" else "10:42",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                Text(text = "5G • 120Hz", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }

                            if (themeConfig.clockPosition == "Center") {
                                Text(
                                    text = if (themeConfig.showSeconds) "10:42:18" else "10:42",
                                    color = activeColorParsed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (themeConfig.clockPosition == "Right") {
                                Text(
                                    text = if (themeConfig.showSeconds) "10:42:18" else "10:42",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                Text(text = "100%", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mock Quick Settings Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MockQsTile("Wi-Fi", "Connected", activeColorParsed, true, Modifier.weight(1f))
                        MockQsTile("Bluetooth", "Active", activeColorParsed, true, Modifier.weight(1f))
                        if (themeConfig.qsColumns == 3) {
                            MockQsTile("Thermal", "Turbo", activeColorParsed, false, Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mock Glassmorphism Blur Notification
                    val blurOpacity = (themeConfig.blurRadius / 100f).coerceIn(0.2f, 0.9f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                activeColorParsed.copy(alpha = blurOpacity * 0.15f),
                                RoundedCornerShape(10.dp)
                            )
                            .border(1.dp, activeColorParsed.copy(alpha = blurOpacity * 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "System Glassmorphic Blur • ${themeConfig.blurRadius}%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Overlay active with adaptive icon shape: ${themeConfig.iconShape}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Monet Accent Color Palette
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MATERIAL YOU ACCENT PALETTE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        accentColors.forEach { (hex, name) ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = themeConfig.accentColorHex.equals(hex, ignoreCase = true)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.updateThemeConfig { it.copy(accentColorHex = hex) }
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(color, CircleShape)
                                        .border(
                                            if (isSelected) 2.5.dp else 1.dp,
                                            if (isSelected) Color.White else Color.Transparent,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = if (hex == "#E2E8F0") Color.Black else Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name.substringBefore(" "),
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Adaptive Icon Shapes
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ADAPTIVE ICON SHAPE (RRO)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconShapes.forEach { shape ->
                            val isSelected = themeConfig.iconShape.equals(shape, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) activeColorParsed.copy(alpha = 0.2f) else CyberDark,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColorParsed else CyberSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.updateThemeConfig { it.copy(iconShape = shape) }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = shape,
                                    color = if (isSelected) activeColorParsed else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Status Bar Clock & Quick Settings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "STATUS BAR & QUICK SETTINGS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Clock Position", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        clockPositions.forEach { pos ->
                            val isSelected = themeConfig.clockPosition.equals(pos, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) activeColorParsed.copy(alpha = 0.2f) else CyberDark,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColorParsed else CyberSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.updateThemeConfig { it.copy(clockPosition = pos) }
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pos,
                                    color = if (isSelected) activeColorParsed else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
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
                        Text(text = "Display Seconds in Status Clock", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Switch(
                            checked = themeConfig.showSeconds,
                            onCheckedChange = { value ->
                                viewModel.updateThemeConfig { it.copy(showSeconds = value) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = activeColorParsed)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Quick Settings 3-Column Grid", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Switch(
                            checked = themeConfig.qsColumns == 3,
                            onCheckedChange = { value ->
                                viewModel.updateThemeConfig { it.copy(qsColumns = if (value) 3 else 2) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = activeColorParsed)
                        )
                    }
                }
            }
        }

        // Section 4: Blur & Refresh Rate & Animations
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PERFORMANCE & MOTION TWEAKS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Notification Glass Blur", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text(
                            text = "${themeConfig.blurRadius}%",
                            color = activeColorParsed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = themeConfig.blurRadius.toFloat(),
                        onValueChange = { value ->
                            viewModel.updateThemeConfig { it.copy(blurRadius = value.toInt()) }
                        },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = activeColorParsed,
                            activeTrackColor = activeColorParsed
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Force Display Refresh Rate", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        refreshRates.forEach { hz ->
                            val isSelected = themeConfig.refreshRateHz == hz
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) activeColorParsed.copy(alpha = 0.2f) else CyberDark,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColorParsed else CyberSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.updateThemeConfig { it.copy(refreshRateHz = hz) }
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${hz}Hz",
                                    color = if (isSelected) activeColorParsed else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "System Window Animation Scale", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        animationScales.forEach { scale ->
                            val isSelected = themeConfig.animationScale == scale
                            val label = if (scale == 0.0f) "Off" else "${scale}x"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) activeColorParsed.copy(alpha = 0.2f) else CyberDark,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColorParsed else CyberSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.updateThemeConfig { it.copy(animationScale = scale) }
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) activeColorParsed else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Apply Overlays CTA
        item {
            Button(
                onClick = { viewModel.requestApplyThemeOverrides() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_theme_overrides_button"),
                colors = ButtonDefaults.buttonColors(containerColor = activeColorParsed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Apply Theme Overrides to System",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MockQsTile(
    title: String,
    status: String,
    accentColor: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) accentColor.copy(alpha = 0.25f) else CyberSurfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                if (isActive) accentColor else CyberSurfaceBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = title,
                color = if (isActive) Color.White else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = status,
                color = if (isActive) accentColor else Color(0xFF64748B),
                fontSize = 9.5.sp
            )
        }
    }
}
