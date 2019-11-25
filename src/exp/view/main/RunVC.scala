package exp.view.main

import java.nio.file.Paths

import utopia.flow.util.CollectionExtensions._
import utopia.flow.async.AsyncExtensions._
import exp.model.logic.ExportAndRun
import utopia.reflection.shape.LengthExtensions._
import exp.model.ui.{ColorScheme, Margins}
import utopia.genesis.image.Image
import utopia.reflection.component.swing.button.{ButtonImageSet, ImageAndTextButton}
import utopia.reflection.localization.Localizer
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}

import scala.concurrent.{ExecutionContext, Future}

/**
 * A view-controller for the main view
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
class RunVC(val target: ExportAndRun)(implicit cb: ComponentContextBuilder, lCode: String,
									  localizer: Localizer, margins: Margins, colorScheme: ColorScheme, exc: ExecutionContext)
{
	private val button =
	{
		implicit val headerContext: ComponentContext = cb.result
		val playImage = Image.readOrEmpty(Paths.get("images/play.png")).withAlpha(0.88)
		val images = ButtonImageSet(playImage, playImage, playImage * 1.2, playImage.withAlpha(0.55))
		ImageAndTextButton.contextual(images, s"Run ${target.name}"){ () => execute() }
	}
	
	/**
	 * A view of this VC
	 */
	val view = button.framed(margins.medium.any x margins.medium.downscaling, colorScheme.primary.dark)
	
	private def execute(): Unit =
	{
		Future
		{
			button.isEnabled = false
			println("Running")
			val (wasSuccess, asyncCompletions) = target(println)
			if (!wasSuccess)
				println("Run failed")
			if (asyncCompletions.nonEmpty)
			{
				println("Waiting for asynchronous processes to finish")
				asyncCompletions.waitFor().map { _.flatten }.flatMap { _.failure }.foreach { e => e.printStackTrace() }
			}
			println("Run finished")
			button.isEnabled = true
		}
	}
}
