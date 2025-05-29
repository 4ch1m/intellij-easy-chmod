package de.achimonline.easychmod.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileSystemEntry
import de.achimonline.easychmod.base.EasyChmodFilePermissions
import de.achimonline.easychmod.bundle.EasyChmodBundle.message
import de.achimonline.easychmod.statusbar.EasyChmodStatusBarWidget
import kotlin.io.path.Path
import kotlin.io.path.getPosixFilePermissions

class EasyChmodAction : DumbAwareAction(message("action.text")) {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val selected = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)?.first() ?: return
        val path = Path(selected.path)

        if (selected is VirtualFileSystemEntry) {
            EasyChmodActionDialog(
                path,
                EasyChmodFilePermissions.fromPosixFilePermissions(path.getPosixFilePermissions())
            ).showAndExecute()

            EasyChmodStatusBarWidget.updateTextRequests.tryEmit(Unit)
        }
    }
}
