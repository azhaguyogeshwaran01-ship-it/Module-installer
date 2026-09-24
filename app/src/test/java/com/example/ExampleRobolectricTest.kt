package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ModuleEntity
import com.example.model.CompatibilityStatus
import com.example.model.DeviceSystemInfo
import com.example.model.RootStatus
import com.example.model.SELinuxMode
import com.example.model.SystemPartitionInfo
import com.example.service.CompatibilityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Module Inserter", appName)
    }

    @Test
    fun `test compatibility engine dynamic adaptation on modern security patch`() {
        val modernDevice = DeviceSystemInfo(
            deviceModel = "Pixel 9 Pro",
            manufacturer = "Google",
            androidVersion = "15",
            apiLevel = 35,
            securityPatch = "2026-05-01",
            kernelRelease = "6.6.21-android15-11-g897f2",
            rootStatus = RootStatus.ROOTED_MAGISK,
            selinuxMode = SELinuxMode.ENFORCING,
            dmVerityStatus = "Enforcing",
            activeMountMethod = "OverlayFS",
            partitions = listOf(
                SystemPartitionInfo(
                    name = "system",
                    mountPoint = "/system",
                    fsType = "erofs",
                    totalBytes = 4294967296L,
                    freeBytes = 104857600L,
                    isReadOnly = true,
                    hasOverlay = true
                )
            )
        )

        val patchAudit = CompatibilityEngine.auditDeviceSecurityPatch(modernDevice)
        assertNotNull(patchAudit)
        assertTrue(patchAudit.supports16KbPages)
        assertTrue(patchAudit.activeShims.isNotEmpty())

        val partitionModule = ModuleEntity(
            id = "test_partition_patch",
            title = "Test EROFS Patch",
            author = "Tester",
            version = "1.0",
            versionCode = 1,
            description = "Tests dynamic compatibility shim",
            category = "KERNEL_PERF",
            isEnabled = false,
            isRootRequired = true,
            supportsNonRoot = false,
            isPartitionPatch = true,
            patchMountTarget = "/system/etc/thermal-engine.conf",
            scriptHook = "post-fs-data",
            configParams = "{}"
        )

        val report = CompatibilityEngine.evaluateModule(partitionModule, modernDevice)
        assertEquals(CompatibilityStatus.COMPATIBLE_WITH_SHIM, report.status)
        assertNotNull(report.appliedShim)
        assertTrue(report.isSafeToApply)
    }
}
