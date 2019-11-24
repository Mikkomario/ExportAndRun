package exp.view.main

import utopia.reflection.shape.LengthExtensions._
import exp.model.ui.{ColorScheme, Margins}
import utopia.reflection.component.swing.button.TextButton
import utopia.reflection.component.swing.label.TextLabel
import utopia.reflection.container.stack.StackLayout.Trailing
import utopia.reflection.container.swing.Stack
import utopia.reflection.localization.{LocalizedString, Localizer}
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}

/**
 * Shows an error message and lets the user close the program
 * @author Mikko Hilpinen
 * @since 24.11.2019, v1+
 */
class ErrorVC(message: LocalizedString)(implicit baseCB: ComponentContextBuilder, margins: Margins,
										colorScheme: ColorScheme, defaultLangCode: String, localizer: Localizer)
{
	// ATTRIBUTES	------------------------
	
	private implicit val baseContext: ComponentContext = baseCB.result
	
	/**
	 * View handled by this VC
	 */
	val view = Stack.buildColumnWithContext(layout = Trailing) { s =>
		s += TextLabel.contextual(message)
		s += TextButton.contextual("Exit", () => System.exit(0))
	}.framed(margins.medium.any.square, colorScheme.gray.light)
}
