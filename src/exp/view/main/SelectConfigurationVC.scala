package exp.view.main

import utopia.reflection.shape.LengthExtensions._
import utopia.reflection.localization.LocalString._
import exp.model.logic.ExportAndRun
import exp.model.ui.{ColorScheme, Margins}
import utopia.reflection.component.swing.button.TextButton
import utopia.reflection.container.swing.Stack
import utopia.reflection.localization.Localizer
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}

/**
 * Lets the user select which configuration should be used this time
 * @author Mikko Hilpinen
 * @since 24.11.2019, v1+
 */
class SelectConfigurationVC(val options: Vector[ExportAndRun], configSelected: ExportAndRun => Unit)
						   (implicit baseCB: ComponentContextBuilder, margins: Margins, colorScheme: ColorScheme,
							defaultLangCode: String, localizer: Localizer)
{
	// ATTRIBUTES	-------------------------
	
	private implicit val baseContext: ComponentContext = baseCB.result
	
	/**
	 * View handled by this VC
	 */
	val view = Stack.buildColumnWithContext() { s =>
		options.foreach { config =>
			s += TextButton.contextual(config.name.localizationSkipped, () => configSelected(config))
		}
	}.framed(margins.medium.downscaling x margins.medium.any, colorScheme.gray.light)
}
