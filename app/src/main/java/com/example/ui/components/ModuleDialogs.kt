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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.CyberCyanBright
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalText
import java.io.File

@Composable
fun RescueZipDialog(
    file: File?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FolderZip,
                    contentDescription = "Rescue Archive",
                    tint = CyberEmerald,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Rescue Package Ready",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "A flashable bootloop rescue archive has been generated and validated for latest Android security patches.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "LOCATION: ${file?.absolutePath ?: "cache/Module_Inserter_Rescue.zip"}",
                            color = TerminalText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "SIZE: ${file?.length() ?: 2450} bytes | CHECKSUM: OK",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• In case of boot failure, flash this zip via TWRP, OrangeFox, or fastboot update.\n• It instantly unmounts all injected modules and forces safe-mode boot without wiping user data.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                modifier = Modifier.testTag("rescue_dialog_confirm")
            ) {
                Text("Got It", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ModuleInspectDialog(
    module: ModuleEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onCheckCompatibility: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = module.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "ID: ${module.id} • v${module.version}",
                        color = CyberCyanBright,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = module.description,
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "System Mounting Specs:",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "TARGET: ${module.patchMountTarget}",
                            color = TerminalText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "HOOK: ${module.scriptHook}.sh",
                            color = TerminalText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "ROOT REQUIRED: ${if (module.isRootRequired) "YES" else "NO (Shizuku RRO Compatible)"}",
                            color = if (module.isRootRequired) Color(0xFFF59E0B) else CyberEmerald,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "PARTITION PATCH: ${if (module.isPartitionPatch) "YES (OverlayFS)" else "NO (Virtual)"}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "module.prop preview:",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBackground, RoundedCornerShape(6.dp))
                        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "id=${module.id}\nname=${module.title}\nversion=${module.version}\nversionCode=${module.versionCode}\nauthor=${module.author}\ndescription=${module.description}",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onCheckCompatibility != null) {
                    OutlinedButton(
                        onClick = onCheckCompatibility,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyanBright)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Audit", fontSize = 11.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberRed),
                        modifier = Modifier.testTag("delete_module_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remove", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright)
                    ) {
                        Text("Close", color = Color.Black, fontSize = 11.sp)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewModuleDialog(
    onDismiss: () -> Unit,
    onCreate: (
        title: String,
        author: String,
        version: String,
        category: String,
        description: String,
        mountTarget: String,
        isPartitionPatch: Boolean,
        isRootOnly: Boolean,
        scriptHook: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0.0") }
    var category by remember { mutableStateOf("KERNEL_PERF") }
    var description by remember { mutableStateOf("") }
    var mountTarget by remember { mutableStateOf("/system/etc") }
    var isPartitionPatch by remember { mutableStateOf(false) }
    var isRootOnly by remember { mutableStateOf(false) }
    var scriptHook by remember { mutableStateOf("post-fs-data") }

    val categories = listOf(
        "KERNEL_PERF" to "Kernel & CPU",
        "THEME_OVERRIDE" to "Theme Overlay",
        "SYSTEM_UI" to "SystemUI Mod",
        "AUDIO_HAL" to "Audio HAL",
        "STABILITY_PATCH" to "Stability Patch",
        "MEMORY_RAM" to "RAM Optimizer",
        "CUSTOM" to "Custom Module"
    )

    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "New Module", tint = CyberCyanBright)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create / Import Module", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Module Name") },
                    placeholder = { Text("e.g. Axial Thermal Tweaker") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_module_title")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Version") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categories.firstOrNull { it.first == category }?.second ?: category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { (catId, catLabel) ->
                            DropdownMenuItem(
                                text = { Text(catLabel) },
                                onClick = {
                                    category = catId
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = mountTarget,
                    onValueChange = { mountTarget = it },
                    label = { Text("Partition Mount Target") },
                    placeholder = { Text("/system/etc or /vendor/etc") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Partition Patch (OverlayFS)", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isPartitionPatch,
                        onCheckedChange = { isPartitionPatch = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberCyanBright)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Strict Root Required", color = Color.White, fontSize = 13.sp)
                    Switch(
                        checked = isRootOnly,
                        onCheckedChange = { isRootOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberCyanBright)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(
                            title, author, version, category,
                            description, mountTarget, isPartitionPatch, isRootOnly, scriptHook
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanBright),
                modifier = Modifier.testTag("confirm_create_module")
            ) {
                Text("Create Module", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
