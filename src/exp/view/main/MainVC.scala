package exp.view.main

import java.nio.file.Paths

import utopia.reflection.shape.LengthExtensions._
import exp.model.ui.{ColorScheme, Margins}
import utopia.genesis.image.Image
import utopia.reflection.component.swing.button.{ButtonImageSet, ImageAndTextButton}
import utopia.reflection.localization.Localizer
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}

/**
 * A view-controller for the main view
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
class MainVC(implicit cb: ComponentContextBuilder, lCode: String, localizer: Localizer, margins: Margins, colorScheme: ColorScheme)
{
	/**
	 * A view of this VC
	 */
	val view =
	{
		implicit val headerContext: ComponentContext = cb.result
		val playImage = Image.readOrEmpty(Paths.get("resources/images/play.png")).withAlpha(0.88)
		val images = ButtonImageSet(playImage, playImage, playImage * 1.2, playImage.withAlpha(0.55))
		ImageAndTextButton.contextual(images, "Run ???")(execute)
			.framed(margins.medium.any x margins.medium.downscaling, colorScheme.primary.dark)
	}
	
	// TODO: implement
	private def execute() = println("Running")
}
