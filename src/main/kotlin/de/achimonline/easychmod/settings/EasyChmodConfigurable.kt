package de.achimonline.easychmod.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.ItemRemovable
import de.achimonline.easychmod.bundle.EasyChmodBundle.message
import de.achimonline.easychmod.settings.EasyChmodConfigurable.EasyChmodPresetsTableColumns.*
import java.util.*
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

class EasyChmodConfigurable : BoundConfigurable(message("settings.display.name")) {
    private var storedSettings = EasyChmodSettingsState.instance.settings
    private var editedSettings = EasyChmodSettings.fromJson(storedSettings.toJson())

    private val heartIcon = IconLoader.getIcon("/icons/heart-solid.svg", EasyChmodConfigurable::class.java)

    private lateinit var dialogPanel: DialogPanel

    private lateinit var presetsTable: JBTable
    private lateinit var presetsTableModel: EasyChmodPresetsTableModel

    override fun createPanel(): DialogPanel {
        presetsTableModel = EasyChmodPresetsTableModel()
        presetsTable = JBTable(presetsTableModel).apply {
            setShowGrid(true)

            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
            emptyText.text = message("settings.presets.empty")

            EasyChmodPresetsTableColumns.entries.forEach { column ->
                columnModel.getColumn(column.ordinal).apply {
                    headerValue = model.getColumnName(column.ordinal)
                }
            }
        }

        val decoratedPresetsTable = ToolbarDecorator.createDecorator(presetsTable).apply {
            setAddAction { addPreset() }
            setRemoveAction { removePreset() }
            setMoveUpAction { movePresetUp() }
            setMoveDownAction { movePresetDown() }
            setEditAction { editPreset() }
        }.createPanel()

        dialogPanel = panel {
            group(message("settings.statusbar")) {
                row {
                    label(message("settings.statusbar.display.format"))
                    comboBox(
                        EnumComboBoxModel(EasyChmodStatusBarDisplayFormat::class.java),
                        textListCellRenderer {
                        when (it) {
                            EasyChmodStatusBarDisplayFormat.SYMBOLIC -> message("settings.display.format.symbolic")
                            EasyChmodStatusBarDisplayFormat.OCTAL -> message("settings.display.format.octal")
                            else -> ""
                        }
                    }).component.apply {
                        selectedItem = editedSettings.statusBarDisplayFormat

                        addItemListener {
                            editedSettings.statusBarDisplayFormat = it.item as EasyChmodStatusBarDisplayFormat
                        }
                    }
                }
            }

            group(message("settings.presets")) {
                row {
                    cell(decoratedPresetsTable).align(AlignX.FILL)
                }
            }

            group {
                row {
                    icon(heartIcon)
                    text(
                        message(
                            "settings.feedback",
                            "https://paypal.me/AchimSeufert",
                            "https://github.com/4ch1m/intellij-easy-chmod",
                            "https://plugins.jetbrains.com/vendor/a04b3664-b76d-4d32-9bd8-71f948b7c97c" // TODO replace with actual plugin link
                        )
                    )
                }
            }
        }

        return dialogPanel
    }

    override fun apply() {
        storedSettings.apply {
            statusBarDisplayFormat = editedSettings.statusBarDisplayFormat

            presets.clear()
            presets.addAll(editedSettings.presets)
        }
    }

    override fun isModified(): Boolean {
        return storedSettings.toJson() != editedSettings.toJson()
    }

    override fun reset() {
        editedSettings = EasyChmodSettings.fromJson(storedSettings.toJson())
        dialogPanel.reset()
    }

    private enum class EasyChmodPresetsTableColumns {
        DESCRIPTION { override fun message() = message("settings.presets.table.column.description") },
        FILE_TYPE { override fun message() = message("settings.presets.table.column.type") },
        REGEX { override fun message() = message("settings.presets.table.column.regex") },
        PERMISSIONS { override fun message() = message("settings.presets.table.column.permissions") };

        abstract fun message(): String
    }

    private inner class EasyChmodPresetsTableModel() : AbstractTableModel(), ItemRemovable {
        override fun getRowCount(): Int {
            return editedSettings.presets.size
        }

        override fun getColumnCount(): Int {
            return EasyChmodPresetsTableColumns.entries.size
        }

        override fun getColumnName(column: Int): String? {
            return when (column) {
                DESCRIPTION.ordinal -> DESCRIPTION.message()
                FILE_TYPE.ordinal -> FILE_TYPE.message()
                REGEX.ordinal -> REGEX.message()
                PERMISSIONS.ordinal -> PERMISSIONS.message()
                else -> ""
            }
        }

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
            val preset = editedSettings.presets[rowIndex]

            return when (columnIndex) {
                DESCRIPTION.ordinal -> preset.description
                FILE_TYPE.ordinal -> preset.fileType
                REGEX.ordinal -> preset.regex
                PERMISSIONS.ordinal -> "${preset.permissions.allSymbols()} (${preset.permissions.allOctals()})"
                else -> ""
            }
        }

        override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
            val preset = editedSettings.presets[rowIndex]

            if (columnIndex == DESCRIPTION.ordinal) {
                preset.description = aValue as String
            }

            if (columnIndex == REGEX.ordinal) {
                preset.regex = aValue as String
            }
        }

        override fun removeRow(index: Int) {
            editedSettings.presets.removeAt(index)
            presetsTableModel.fireTableDataChanged()
        }

        override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
            return listOf(DESCRIPTION.ordinal, REGEX.ordinal).contains(columnIndex)
        }
    }

    private fun movePresetUp() {
        Collections.swap(editedSettings.presets, presetsTable.selectedRow, presetsTable.selectedRow - 1)
        presetsTableModel.fireTableDataChanged()
    }

    private fun movePresetDown() {
        Collections.swap(editedSettings.presets, presetsTable.selectedRow, presetsTable.selectedRow + 1)
        presetsTableModel.fireTableDataChanged()
    }

    private fun removePreset() {
        editedSettings.presets.removeAt(presetsTable.selectedRow)
        presetsTableModel.fireTableDataChanged()
    }

    private fun addPreset() {
        val presetToBeAdded = EasyChmodPreset()
        if (EasyChmodPresetDialog(presetToBeAdded).showAndGet()) {
            editedSettings.presets.add(presetsTable.selectedRow + 1, presetToBeAdded)
            presetsTableModel.fireTableDataChanged()
        }
    }

    private fun editPreset() {
        val selectedPreset = editedSettings.presets[presetsTable.selectedRow]
        val presetToBeEdited = EasyChmodPreset(
            selectedPreset.description,
            selectedPreset.fileType,
            selectedPreset.regex,
            selectedPreset.permissions.copy()
        )

        if (EasyChmodPresetDialog(presetToBeEdited).showAndGet()) {
            editedSettings.presets[presetsTable.selectedRow] = presetToBeEdited
            presetsTableModel.fireTableDataChanged()
        }
    }
}
