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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.ModuleCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import com.example.ui.viewmodel.ModuleViewModel

@Composable
fun ModulesScreen(
    viewModel: ModuleViewModel,
    modifier: Modifier = Modifier
) {
    val modules by viewModel.modules.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()

    val categories = listOf(
        "ALL" to "All Modules",
        "KERNEL_PERF" to "Kernel & CPU",
        "THEME_OVERRIDE" to "Themes",
        "SYSTEM_UI" to "SystemUI",
        "AUDIO_HAL" to "Audio HAL",
        "STABILITY_PATCH" to "Security Patch",
        "MEMORY_RAM" to "RAM / ZRAM"
    )

    val filteredModules = modules.filter { module ->
        val matchesCategory = uiState.selectedCategory == "ALL" || module.category == uiState.selectedCategory
        val matchesSearch = uiState.searchQuery.isBlank() ||
                module.title.contains(uiState.searchQuery, ignoreCase = true) ||
                module.description.contains(uiState.searchQuery, ignoreCase = true) ||
                module.id.contains(uiState.searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MODULE INSERTER HUB",
                            color = CyberCyanBright,
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${modules.count { it.isEnabled }} active • ${modules.size} total packages",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Security Patch Compatibility Alert Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground, RoundedCornerShape(12.dp))
                        .border(1.2.dp, CyberEmerald.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.openSystemPatchAuditDialog() }
                        .padding(12.dp)
                        .testTag("security_patch_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PATCH COMPATIBILITY: LEVEL ${deviceInfo.securityPatch}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Dynamic shims active for Android ${deviceInfo.androidVersion} • Tap to view audit",
                                    color = TerminalText,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        StatusBadge(text = "SCANNER", color = CyberEmerald)
                    }
                }
            }

            // Search Box
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search modules, patches, audio...", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyanBright)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_modules_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = CyberCyanBright,
                        unfocusedBorderColor = CyberSurfaceBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Category Filter Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (catKey, catLabel) ->
                        val isSelected = uiState.selectedCategory == catKey
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) CyberCyanBright else CyberSurface,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) CyberCyanBright else CyberSurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setCategoryFilter(catKey) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = catLabel,
                                color = if (isSelected) Color.Black else Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            if (filteredModules.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No matching modules found",
                            color = Color(0xFFCBD5E1),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredModules, key = { it.id }) { module ->
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
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Install / Add Module
        FloatingActionButton(
            onClick = { viewModel.setShowNewModuleDialog(true) },
            containerColor = CyberCyanBright,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_module")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Module")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Insert Module", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
