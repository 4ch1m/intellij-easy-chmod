package de.achimonline.easychmod.settings

import com.google.gson.Gson

import de.achimonline.easychmod.base.EasyChmodFilePermissions
import de.achimonline.easychmod.settings.EasyChmodPreset.FileType.DIRECTORY
import de.achimonline.easychmod.settings.EasyChmodPreset.FileType.FILE


enum class EasyChmodStatusBarDisplayFormat {
    SYMBOLIC,
    OCTAL
}

data class EasyChmodPreset(
    var description: String = "preset description",
    var fileType: FileType = FILE,
    var regex: String = ".*",
    var permissions: EasyChmodFilePermissions = EasyChmodFilePermissions()
) {
    enum class FileType {
        FILE,
        DIRECTORY
    }
}

data class EasyChmodSettings(
    var statusBarDisplayFormat: EasyChmodStatusBarDisplayFormat = EasyChmodStatusBarDisplayFormat.SYMBOLIC,
    var presets: MutableList<EasyChmodPreset> = mutableListOf(
        EasyChmodPreset(
            description = "Shell script",
            fileType = FILE,
            regex = ".*\\.sh$",
            permissions = EasyChmodFilePermissions.fromOctals(Triple(7, 5, 5))
        ),
        EasyChmodPreset(
            description = "Environment file",
            fileType = FILE,
            regex = ".*\\.(env|ENV)$",
            permissions = EasyChmodFilePermissions.fromOctals(Triple(6, 0, 0))
        ),
        EasyChmodPreset(
            description = "Secure",
            fileType = FILE,
            regex = "",
            permissions = EasyChmodFilePermissions.fromOctals(Triple(6, 0, 0))
        ),
        EasyChmodPreset(
            description = "Secure",
            fileType = DIRECTORY,
            regex = "",
            permissions = EasyChmodFilePermissions.fromOctals(Triple(7, 0, 0))
        ),
        EasyChmodPreset(
            description = "Unrestricted",
            fileType = FILE,
            regex = "",
            permissions = EasyChmodFilePermissions.fromOctals(Triple(6, 6, 6))
        ),
        EasyChmodPreset(
            description = "Unrestricted",
            fileType = DIRECTORY,
            regex = "",
            permissions = EasyChmodFilePermissions.fromOctals(Triple(7, 7, 7))
        )
    )
) {
    fun toJson() : String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String) : EasyChmodSettings {
            return Gson().fromJson(json, EasyChmodSettings::class.java)
        }
    }
}
