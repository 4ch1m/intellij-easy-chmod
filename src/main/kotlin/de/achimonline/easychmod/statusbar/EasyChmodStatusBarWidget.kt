package de.achimonline.easychmod.statusbar

import com.intellij.openapi.application.readAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.TextWidgetPresentation
import com.intellij.openapi.wm.WidgetPresentationDataContext
import com.intellij.openapi.wm.impl.status.EditorBasedWidgetHelper
import com.intellij.openapi.wm.impl.status.PositionPanel.Companion.DISABLE_FOR_EDITOR
import de.achimonline.easychmod.base.EasyChmodFilePermissions
import de.achimonline.easychmod.bundle.EasyChmodBundle.message
import de.achimonline.easychmod.action.EasyChmodActionDialog
import de.achimonline.easychmod.settings.EasyChmodSettingsState
import de.achimonline.easychmod.settings.EasyChmodStatusBarDisplayFormat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.awt.Component
import java.awt.event.MouseEvent
import kotlin.io.path.Path
import kotlin.io.path.getPosixFilePermissions
import kotlin.time.Duration.Companion.milliseconds

class EasyChmodStatusBarWidget(
    private val dataContext: WidgetPresentationDataContext,
    private val widgetHelper: EditorBasedWidgetHelper = EditorBasedWidgetHelper(dataContext.project)
) : TextWidgetPresentation {
    private val settings = EasyChmodSettingsState.instance.settings

    init {
        updateTextRequests = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).also { it.tryEmit(Unit) }
    }

    override val alignment: Float
        get() = Component.CENTER_ALIGNMENT

    override fun text(): Flow<@NlsContexts.Label String?> {
        return combine(
            updateTextRequests,
            dataContext.currentFileEditor
        ) { _, fileEditor -> (fileEditor as? TextEditor)?.editor }
            .debounce(100.milliseconds)
            .mapLatest { editor ->
                if (editor == null || DISABLE_FOR_EDITOR.isIn(editor)) null else readAction { getDisplayString(editor) }
            }
    }

    override suspend fun getTooltipText(): @NlsContexts.Tooltip String? {
        return message("statusbar.tooltip")
    }

    override fun getClickConsumer(): ((MouseEvent) -> Unit)? {
        return h@{
            val project = widgetHelper.project
            val filePath = dataContext.currentFileEditor.value?.file?.path

            if (filePath != null) {
                val path = Path(filePath)

                EasyChmodActionDialog(
                    project,
                    path,
                    EasyChmodFilePermissions.fromPosixFilePermissions(path.getPosixFilePermissions())
                ).showAndExecute()

                updateTextRequests.tryEmit(Unit)
            }
        }
    }

    private fun getDisplayString(editor: Editor): @NlsContexts.Label String {
        if (editor.isDisposed) return ""

        val posixFilePermissions = Path(editor.virtualFile.path).getPosixFilePermissions()
        val easyChmodFilePermissions = EasyChmodFilePermissions.fromPosixFilePermissions(posixFilePermissions)

        return if (settings.statusBarDisplayFormat == EasyChmodStatusBarDisplayFormat.SYMBOLIC) {
            "[ ${easyChmodFilePermissions.allSymbols()} ]"
        } else {
            "[ ${easyChmodFilePermissions.allOctals()} ]"
        }
    }

    companion object {
        lateinit var updateTextRequests : MutableSharedFlow<Unit>
    }
}
