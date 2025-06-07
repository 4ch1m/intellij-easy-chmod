package de.achimonline.easychmod.settings

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty
import com.intellij.ui.layout.ValidationInfoBuilder
import de.achimonline.easychmod.bundle.EasyChmodBundle
import de.achimonline.easychmod.bundle.EasyChmodBundle.message
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.swing.JComponent
import javax.swing.JLabel

class EasyChmodPresetDialog(val easyChmodPreset: EasyChmodPreset) : DialogWrapper(null) {
    private lateinit var dialogPanel: DialogPanel

    private lateinit var ownerOctalLabel: JLabel
    private lateinit var groupOctalLabel: JLabel
    private lateinit var othersOctalLabel: JLabel

    private lateinit var ownerSymbolLabel: JLabel
    private lateinit var groupSymbolLabel: JLabel
    private lateinit var othersSymbolLabel: JLabel

    init {
        title = EasyChmodBundle.message("dialog.preset.title")
        init()
    }

    override fun createCenterPanel(): JComponent? {
        dialogPanel = panel {
            row(EasyChmodBundle.message("dialog.preset.description")) {
                textField().bindText(easyChmodPreset::description)
            }

            row(EasyChmodBundle.message("dialog.preset.filetype")) {
                comboBox(EnumComboBoxModel(EasyChmodPreset.FileType::class.java))
                    .bindItem(easyChmodPreset::fileType.toNullableProperty())
            }

            row(EasyChmodBundle.message("dialog.preset.regex")) {
                textField()
                    .bindText(easyChmodPreset::regex)
                    .validationInfo {
                        validatePresetRegEx(it.text)
                    }
            }

            row {
                cell()

                listOf(
                    EasyChmodBundle.message("dialog.preset.column.owner"),
                    EasyChmodBundle.message("dialog.preset.column.group"),
                    EasyChmodBundle.message("dialog.preset.column.others")
                ).forEach {
                    label(it).align(AlignX.CENTER)
                }

                cell()
            }.topGap(TopGap.SMALL).layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.preset.row.read")).align(AlignX.RIGHT)

                listOf(
                    easyChmodPreset.permissions::ownerRead,
                    easyChmodPreset.permissions::groupRead,
                    easyChmodPreset.permissions::othersRead
                ).forEach {
                    checkBox("").bindSelected(it).onChanged { updateOctalsAndSymbols() }.align(AlignX.CENTER)
                }

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.preset.row.write")).align(AlignX.RIGHT)

                listOf(
                    easyChmodPreset.permissions::ownerWrite,
                    easyChmodPreset.permissions::groupWrite,
                    easyChmodPreset.permissions::othersWrite
                ).forEach {
                    checkBox("").bindSelected(it).onChanged { updateOctalsAndSymbols() }.align(AlignX.CENTER)
                }

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.preset.row.execute")).align(AlignX.RIGHT)

                listOf(
                    easyChmodPreset.permissions::ownerExecute,
                    easyChmodPreset.permissions::groupExecute,
                    easyChmodPreset.permissions::othersExecute
                ).forEach {
                    checkBox("").bindSelected(it).onChanged { updateOctalsAndSymbols() }.align(AlignX.CENTER)
                }

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            separator()

            row {
                label(EasyChmodBundle.message("dialog.preset.row.octal")).align(AlignX.RIGHT)

                ownerOctalLabel = label("${easyChmodPreset.permissions.ownerOctal()}").enabled(false).align(AlignX.CENTER).component
                groupOctalLabel = label("${easyChmodPreset.permissions.groupOctal()}").enabled(false).align(AlignX.CENTER).component
                othersOctalLabel = label("${easyChmodPreset.permissions.othersOctal()}").enabled(false).align(AlignX.CENTER).component

                cell()
            }.layout(RowLayout.PARENT_GRID).resizableRow()

            row {
                label(EasyChmodBundle.message("dialog.preset.row.symbolic")).align(AlignX.RIGHT)

                ownerSymbolLabel = label(easyChmodPreset.permissions.ownerSymbolic()).enabled(false).align(AlignX.CENTER).component
                groupSymbolLabel = label(easyChmodPreset.permissions.groupSymbolic()).enabled(false).align(AlignX.CENTER).component
                othersSymbolLabel = label(easyChmodPreset.permissions.othersSymbolic()).enabled(false).align(AlignX.CENTER).component

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

        ownerOctalLabel.text = "${easyChmodPreset.permissions.ownerOctal()}"
        ownerSymbolLabel.text = easyChmodPreset.permissions.ownerSymbolic()

        groupOctalLabel.text = "${easyChmodPreset.permissions.groupOctal()}"
        groupSymbolLabel.text = easyChmodPreset.permissions.groupSymbolic()

        othersOctalLabel.text = "${easyChmodPreset.permissions.othersOctal()}"
        othersSymbolLabel.text = easyChmodPreset.permissions.othersSymbolic()
    }

    private fun ValidationInfoBuilder.validatePresetRegEx(regEx: String): ValidationInfo? {
        try {
            Pattern.compile(regEx)
        } catch (_: PatternSyntaxException) {
            return error(message("settings.presets.dialog.invalid.regex"))
        }

        return null
    }
}
