package de.achimonline.easychmod.base

import java.nio.file.attribute.PosixFilePermission

data class EasyChmodFilePermissions(
    var ownerRead: Boolean = false,
    var ownerWrite: Boolean = false,
    var ownerExecute: Boolean = false,
    var groupRead: Boolean = false,
    var groupWrite: Boolean = false,
    var groupExecute: Boolean = false,
    var othersRead: Boolean = false,
    var othersWrite: Boolean = false,
    var othersExecute: Boolean = false
) {
    fun ownerOctal(): Int {
        return octal(ownerRead, ownerWrite, ownerExecute)
    }

    fun ownerSymbolic(): String {
        return symbols(ownerRead, ownerWrite, ownerExecute)
    }

    fun groupOctal(): Int {
        return octal(groupRead, groupWrite, groupExecute)
    }

    fun groupSymbolic(): String {
        return symbols(groupRead, groupWrite, groupExecute)
    }

    fun othersOctal(): Int {
        return octal(othersRead, othersWrite, othersExecute)
    }

    fun othersSymbolic(): String {
        return symbols(othersRead, othersWrite, othersExecute)
    }

    private fun octal(read: Boolean, write: Boolean, execute: Boolean): Int {
        var octal = 0

        if (read) octal += 4
        if (write) octal += 2
        if (execute) octal += 1

        return octal
    }

    private fun symbols(read: Boolean, write: Boolean, execute: Boolean): String {
        val symbols = StringBuilder()

        if (read) symbols.append("r") else symbols.append("-")
        if (write) symbols.append("w") else symbols.append("-")
        if (execute) symbols.append("x") else symbols.append("-")

        return symbols.toString()
    }

    fun allOctals(): String {
        return "${ownerOctal()}${groupOctal()}${othersOctal()}"
    }

    fun allSymbols(): String {
        return "${ownerSymbolic()}${groupSymbolic()}${othersSymbolic()}"
    }

    fun toPosixFilePermissions(): Set<PosixFilePermission> {
        val posixFilePermissions = mutableSetOf<PosixFilePermission>()

        if (ownerRead) posixFilePermissions.add(PosixFilePermission.OWNER_READ)
        if (ownerWrite) posixFilePermissions.add(PosixFilePermission.OWNER_WRITE)
        if (ownerExecute) posixFilePermissions.add(PosixFilePermission.OWNER_EXECUTE)
        if (groupRead) posixFilePermissions.add(PosixFilePermission.GROUP_READ)
        if (groupWrite) posixFilePermissions.add(PosixFilePermission.GROUP_WRITE)
        if (groupExecute) posixFilePermissions.add(PosixFilePermission.GROUP_EXECUTE)
        if (othersRead) posixFilePermissions.add(PosixFilePermission.OTHERS_READ)
        if (othersWrite) posixFilePermissions.add(PosixFilePermission.OTHERS_WRITE)
        if (othersExecute) posixFilePermissions.add(PosixFilePermission.OTHERS_EXECUTE)

        return posixFilePermissions
    }

    companion object {
        fun fromPosixFilePermissions(posixFilePermissions: Set<PosixFilePermission>): EasyChmodFilePermissions {
            return EasyChmodFilePermissions(
                ownerRead = posixFilePermissions.contains(PosixFilePermission.OWNER_READ),
                ownerWrite = posixFilePermissions.contains(PosixFilePermission.OWNER_WRITE),
                ownerExecute = posixFilePermissions.contains(PosixFilePermission.OWNER_EXECUTE),
                groupRead = posixFilePermissions.contains(PosixFilePermission.GROUP_READ),
                groupWrite = posixFilePermissions.contains(PosixFilePermission.GROUP_WRITE),
                groupExecute = posixFilePermissions.contains(PosixFilePermission.GROUP_EXECUTE),
                othersRead = posixFilePermissions.contains(PosixFilePermission.OTHERS_READ),
                othersWrite = posixFilePermissions.contains(PosixFilePermission.OTHERS_WRITE),
                othersExecute = posixFilePermissions.contains(PosixFilePermission.OTHERS_EXECUTE)
            )
        }

        fun fromOctals(octals: Triple<Int, Int, Int>): EasyChmodFilePermissions {
            return EasyChmodFilePermissions(
                ownerRead = octals.first >= 4,
                ownerWrite = octals.first >= 6,
                ownerExecute = octals.first.mod(2) == 1,
                groupRead = octals.second >= 4,
                groupWrite = octals.second >= 6,
                groupExecute = octals.second.mod(2) == 1,
                othersRead = octals.third >= 4,
                othersWrite = octals.third >= 6,
                othersExecute = octals.third.mod(2) == 1
            )
        }
    }
}
