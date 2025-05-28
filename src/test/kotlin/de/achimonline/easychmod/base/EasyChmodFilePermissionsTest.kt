package de.achimonline.easychmod.base

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EasyChmodFilePermissionsTest {
    @Test
    fun allOctals() {
        assertEquals(
            "000",
            EasyChmodFilePermissions().allOctals()
        )

        assertEquals(
            "754",
            EasyChmodFilePermissions(
                ownerRead = true,
                ownerWrite = true,
                ownerExecute = true,
                groupRead = true,
                groupWrite = false,
                groupExecute = true,
                othersRead = true,
                othersWrite = false,
                othersExecute = false
            ).allOctals()
        )
    }

    @Test
    fun allSymbols() {
        assertEquals(
            "---------",
            EasyChmodFilePermissions().allSymbols()
        )

        assertEquals(
            "rwxr-xr--",
            EasyChmodFilePermissions(
                ownerRead = true,
                ownerWrite = true,
                ownerExecute = true,
                groupRead = true,
                groupWrite = false,
                groupExecute = true,
                othersRead = true,
                othersWrite = false,
                othersExecute = false
            ).allSymbols()
        )
    }

    @Test
    fun fromOctals() {
        assertEquals(
            "600",
            EasyChmodFilePermissions.fromOctals(Triple(6, 0, 0)).allOctals()
        )
        assertEquals(
            "755",
            EasyChmodFilePermissions.fromOctals(Triple(7, 5, 5)).allOctals()
        )
        assertEquals(
            "004",
            EasyChmodFilePermissions.fromOctals(Triple(0, 0, 4)).allOctals()
        )
    }
}
