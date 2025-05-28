package de.achimonline.easychmod.bundle

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE_NAME = "messages.EasyChmodBundle"

object EasyChmodBundle : DynamicBundle(BUNDLE_NAME) {
    fun message(
        @PropertyKey(resourceBundle = BUNDLE_NAME) key: String,
        vararg params: Any
    ): String = getMessage(key, *params)
}
