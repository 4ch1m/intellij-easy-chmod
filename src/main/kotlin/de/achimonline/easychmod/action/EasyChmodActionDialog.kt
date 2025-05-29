package de.achimonline.easychmod.action

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import de.achimonline.easychmod.base.EasyChmodFilePermissions
import de.achimonline.easychmod.bundle.EasyChmodBundle
import de.achimonline.easychmod.settings.EasyChmodPreset
import de.achimonline.easychmod.settings.EasyChmodSettings
import de.achimonline.easychmod.settings.EasyChmodSettingsState
import java.nio.file.Path
import javax.swing.JCheckBox
import javax.swing.JLabel
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.pathString
import kotlin.io.path.setPosixFilePermissions
import kotlin.reflect.KMutableProperty0

class EasyChmodActionDialog(
    val filePath: Path,
    val easyChmodFilePermissions: EasyChmodFilePermissions
) : DialogWrapper(null) {
    private var settings: EasyChmodSettings

    private lateinit var dialogPanel: DialogPanel

    private lateinit var ownerReadCheckBox: JCheckBox
    private lateinit var ownerWriteCheckBox: JCheckBox
    private lateinit var ownerExecuteCheckBox: JCheckBox

    private lateinit var groupReadCheckBox: JCheckBox
    private lateinit var groupWriteCheckBox: JCheckBox
    private lateinit var groupExecuteCheckBox: JCheckBox

    private lateinit var othersReadCheckBox: JCheckBox
    private lateinit var othersWriteCheckBox: JCheckBox
    private lateinit var othersExecuteCheckBox: JCheckBox

    private lateinit var ownerOctalLabel: JLabel
    private lateinit var groupOctalLabel: JLabel
    private lateinit var othersOctalLabel: JLabel

    private lateinit var ownerSymbolLabel: JLabel
    private lateinit var groupSymbolLabel: JLabel
    private lateinit var othersSymbolLabel: JLabel

    init {
        title = EasyChmodBundle.message("dialog.action.title")
        settings = ApplicationManager.getApplication().getService(EasyChmodSettingsState::class.java).settings

        init()
    }

    override fun createCenterPanel(): DialogPanel {
        dialogPanel = panel {
            row {
                text(
                    """
                    <icon src='AllIcons.${if (filePath.isDirectory()) "Nodes.Folder" else "FileTypes.Any_type"}'>
                    &nbsp;
                    ${filePath.fileName}
                """
                ).bold()
            }

            row {
                label(EasyChmodBundle.message("dialog.action.presets"))
                val presets =
                    settings.presets.filter { it.fileType == (if (filePath.isDirectory()) EasyChmodPreset.FileType.DIRECTORY else EasyChmodPreset.FileType.FILE) }
                        .toMutableList()
                presets.addFirst(
                    EasyChmodPreset(
                        description = "default",
                        permissions = EasyChmodFilePermissions(
                            ownerRead = true,
                            ownerWrite = true,
                            ownerExecute = filePath.isDirectory(),
                            groupRead = true,
                            groupWrite = true,
                            groupExecute = filePath.isDirectory(),
                            othersRead = true,
                            othersWrite = false,
                            othersExecute = filePath.isDirectory()
                        )
                    )
                )

                val presetSelection = comboBox(
                    items = presets,
                    renderer = textListCellRenderer { "${it?.description} (${it?.permissions?.allOctals()})" }
                ).component

                run breaking@{
                    presets.forEachIndexed { index, preset ->
                        if (index == 0) return@forEachIndexed // skip "default" entry

                        if (preset.regex.trim() == "") return@forEachIndexed

                        if ((preset.fileType == EasyChmodPreset.FileType.DIRECTORY && filePath.isDirectory()) ||
                            (preset.fileType == EasyChmodPreset.FileType.FILE && filePath.isRegularFile())
                        ) {
                            if (Regex(preset.regex).matches(filePath.fileName.pathString)) {
                                presetSelection.selectedIndex = index
                                return@breaking
                            }
                        }
                    }
                }

                button(EasyChmodBundle.message("dialog.action.presets.apply")) {
                    val presetPermissions = (presetSelection.selectedItem as EasyChmodPreset).permissions
                    easyChmodFilePermissions.apply {
                        ownerReadCheckBox.isSelected = presetPermissions.ownerRead
                        ownerWriteCheckBox.isSelected = presetPermissions.ownerWrite
                        ownerExecuteCheckBox.isSelected = presetPermissions.ownerExecute

                        groupReadCheckBox.isSelected = presetPermissions.groupRead
                        groupWriteCheckBox.isSelected = presetPermissions.groupWrite
                        groupExecuteCheckBox.isSelected = presetPermissions.groupExecute

                        othersReadCheckBox.isSelected = presetPermissions.othersRead
                        othersWriteCheckBox.isSelected = presetPermissions.othersWrite
                        othersExecuteCheckBox.isSelected = presetPermissions.othersExecute
                    }
                }
            }.topGap(TopGap.MEDIUM).bottomGap(BottomGap.SMALL).resizableRow()

            row {
                cell()

                listOf(
                    EasyChmodBundle.message("dialog.action.column.owner"),
                    EasyChmodBundle.message("dialog.action.column.group"),
                    EasyChmodBundle.message("dialog.action.column.others")
                ).forEach {
                    label(it).align(AlignX.CENTER)
                }

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.action.row.read")).align(AlignX.RIGHT)

                val createReadCheckBox = { prop: KMutableProperty0<Boolean> ->
                    checkBox("").bindSelected(prop).onChanged { updateOctalsAndSymbols() }
                        .align(AlignX.CENTER).component
                }
                ownerReadCheckBox = createReadCheckBox(easyChmodFilePermissions::ownerRead)
                groupReadCheckBox = createReadCheckBox(easyChmodFilePermissions::groupRead)
                othersReadCheckBox = createReadCheckBox(easyChmodFilePermissions::othersRead)

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.action.row.write")).align(AlignX.RIGHT)

                val createWriteCheckBox = { prop: KMutableProperty0<Boolean> ->
                    checkBox("").bindSelected(prop).onChanged { updateOctalsAndSymbols() }
                        .align(AlignX.CENTER).component
                }
                ownerWriteCheckBox = createWriteCheckBox(easyChmodFilePermissions::ownerWrite)
                groupWriteCheckBox = createWriteCheckBox(easyChmodFilePermissions::groupWrite)
                othersWriteCheckBox = createWriteCheckBox(easyChmodFilePermissions::othersWrite)

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.action.row.execute")).align(AlignX.RIGHT)

                val createExecuteCheckBox = { prop: KMutableProperty0<Boolean> ->
                    checkBox("").bindSelected(prop).onChanged { updateOctalsAndSymbols() }
                        .align(AlignX.CENTER).component
                }
                ownerExecuteCheckBox = createExecuteCheckBox(easyChmodFilePermissions::ownerExecute)
                groupExecuteCheckBox = createExecuteCheckBox(easyChmodFilePermissions::groupExecute)
                othersExecuteCheckBox = createExecuteCheckBox(easyChmodFilePermissions::othersExecute)

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            separator()

            row {
                label(EasyChmodBundle.message("dialog.action.row.octal")).align(AlignX.RIGHT)

                ownerOctalLabel = label("${easyChmodFilePermissions.ownerOctal()}").enabled(false).align(AlignX.CENTER).component
                groupOctalLabel = label("${easyChmodFilePermissions.groupOctal()}").enabled(false).align(AlignX.CENTER).component
                othersOctalLabel = label("${easyChmodFilePermissions.othersOctal()}").enabled(false).align(AlignX.CENTER).component

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.action.row.symbolic")).align(AlignX.RIGHT)

                ownerSymbolLabel = label(easyChmodFilePermissions.ownerSymbolic()).enabled(false).align(AlignX.CENTER).component
                groupSymbolLabel = label(easyChmodFilePermissions.groupSymbolic()).enabled(false).align(AlignX.CENTER).component
                othersSymbolLabel = label(easyChmodFilePermissions.othersSymbolic()).enabled(false).align(AlignX.CENTER).component

                cell()
            }.bottomGap(BottomGap.MEDIUM).layout(RowLayout.PARENT_GRID).resizableRow()
        }

        dialogPanel.apply {
            isResizable = false
        }

        return dialogPanel
    }

    private fun updateOctalsAndSymbols() {
        dialogPanel.apply()

        ownerOctalLabel.text = "${easyChmodFilePermissions.ownerOctal()}"
        ownerSymbolLabel.text = easyChmodFilePermissions.ownerSymbolic()

        groupOctalLabel.text = "${easyChmodFilePermissions.groupOctal()}"
        groupSymbolLabel.text = easyChmodFilePermissions.groupSymbolic()

        othersOctalLabel.text = "${easyChmodFilePermissions.othersOctal()}"
        othersSymbolLabel.text = easyChmodFilePermissions.othersSymbolic()
    }

    fun showAndExecute() {
        if (showAndGet()) {
            try {
                filePath.setPosixFilePermissions(easyChmodFilePermissions.toPosixFilePermissions())
            } catch (_: Exception) {
                Messages.showErrorDialog(
                    EasyChmodBundle.message("dialog.action.fail.message", filePath.pathString),
                    EasyChmodBundle.message("dialog.action.fail.title")
                )
            }
        }
    }
}
