package de.achimonline.easychmod.statusbar

import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.WidgetPresentation
import com.intellij.openapi.wm.WidgetPresentationDataContext
import com.intellij.openapi.wm.WidgetPresentationFactory
import de.achimonline.easychmod.bundle.EasyChmodBundle.message
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.annotations.NonNls

class EasyChmodStatusBarWidgetFactory : StatusBarWidgetFactory, WidgetPresentationFactory {
    override fun getId(): @NonNls String {
        return ID
    }

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return message("statusbar.display.name")
    }

    override fun createPresentation(context: WidgetPresentationDataContext, scope: CoroutineScope): WidgetPresentation {
        return EasyChmodStatusBarWidget(dataContext = context)
    }

    companion object {
        const val ID = "EasyChmod"
    }
}
