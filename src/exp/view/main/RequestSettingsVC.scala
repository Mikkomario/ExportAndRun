package exp.view.main

import java.nio.file.Path

import exp.model.ui.{ColorScheme, Margins}
import utopia.reflection.shape.LengthExtensions._
import utopia.flow.util.FileExtensions._
import utopia.flow.util.StringExtensions._
import utopia.genesis.color.RGB
import utopia.reflection.component.swing.TextField
import utopia.reflection.component.swing.button.TextButton
import utopia.reflection.component.swing.label.TextLabel
import utopia.reflection.container.swing.Stack
import utopia.reflection.localization.Localizer
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}

/**
 * Used for requesting user about where configuration files should be read from
 * @author Mikko Hilpinen
 * @since 23.11.2019, v1+
 * @param onCompletion A function that is called when the user has selected an existing directory
 */
class RequestSettingsVC(private val onCompletion: Path => Unit)
					   (implicit val baseCB: ComponentContextBuilder, defaultLanguageCode: String, lozalizer: Localizer,
						margins: Margins, colorScheme: ColorScheme)
{
	// ATTRIBUTES	-----------------------
	
	private implicit val baseContext: ComponentContext = baseCB.result
	
	private val inputField = TextField.contextual(prompt = Some("Configuration files directory"))
	inputField.addEnterListener(pathProvided)
	
	private val errorLabel = TextLabel.contextual()(baseCB.withTextColor(RGB.red(0.3)).result)
	errorLabel.isVisible = false
	
	/**
	 * View of this VC
	 */
	val view = Stack.buildColumnWithContext(isRelated = true) { s =>
		s += TextLabel.contextual("Where should I search for configuration (.json) files?")
		s += Stack.buildRowWithContext(isRelated = true) { row =>
			row += inputField
			row += TextButton.contextual("OK", () => pathProvided(inputField.text.notEmpty))
		}
		s += errorLabel
		
	}.framed(margins.medium.any.square, colorScheme.gray.light)
	
	private def pathProvided(path: Option[String]) =
	{
		if (path.nonEmpty)
		{
			val targetPath = path.get: Path
			if (targetPath.isDirectory)
				onCompletion(targetPath)
			else
			{
				errorLabel.text = s"$targetPath doesn't exist or is not a directory"
				errorLabel.isVisible = true
			}
		}
		else
		{
			errorLabel.text = "Please provide a path to the configurations directory"
			errorLabel.isVisible = true
		}
	}
}
