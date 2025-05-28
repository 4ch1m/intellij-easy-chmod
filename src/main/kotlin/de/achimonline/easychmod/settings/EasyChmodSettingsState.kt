package de.achimonline.easychmod.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import de.achimonline.easychmod.statusbar.EasyChmodStatusBarWidget

@State(
    name = "de.achimonline.easychmod.settings.EasyChmodSettingsState",
    storages = [Storage("EasyChmod.xml")]
)
class EasyChmodSettingsState : PersistentStateComponent<EasyChmodSettingsState?> {
    var settings = EasyChmodSettings()

    override fun getState(): EasyChmodSettingsState? {
        EasyChmodStatusBarWidget.updateTextRequests.tryEmit(Unit)

        return this
    }

    override fun loadState(state: EasyChmodSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: EasyChmodSettingsState
            get() = ApplicationManager.getApplication().getService(EasyChmodSettingsState::class.java)
    }
}
