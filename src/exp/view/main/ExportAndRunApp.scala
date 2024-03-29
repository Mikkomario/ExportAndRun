package exp.view.main

import exp.model.ui.{ColorScheme, ColorSet, Margins}
import utopia.flow.async.ThreadPool
import utopia.reflection.shape.LengthExtensions._
import utopia.genesis.generic.GenesisDataType
import utopia.genesis.handling.ActorLoop
import utopia.genesis.handling.mutable.ActorHandler
import utopia.reflection.container.stack.StackHierarchyManager
import utopia.reflection.container.swing.window.Frame
import utopia.reflection.container.swing.window.WindowResizePolicy.Program
import utopia.reflection.localization.{Localizer, NoLocalization}
import utopia.reflection.shape.StackLength
import utopia.reflection.text.Font
import utopia.reflection.util.ComponentContextBuilder

import scala.concurrent.ExecutionContext

/**
 * This is the main application for this project
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
object ExportAndRunApp extends App
{
	GenesisDataType.setup()
	
	// Sets up localization context
	implicit val defaultLanguageCode: String = "EN"
	implicit val localizer: Localizer = NoLocalization
	
	// Creates component context
	implicit val colorScheme: ColorScheme = ColorScheme(
		ColorSet.fromHexes("#006064", "#428e92", "#00363a").get,
		ColorSet.fromHexes("#64dd17", "#9cff57", "#1faa00").get)
	implicit val margins: Margins = Margins(16)
	val actorHandler = ActorHandler()
	implicit val baseContextBuilder: ComponentContextBuilder = ComponentContextBuilder(actorHandler, Font("Arial", 18),
		colorScheme.secondary, colorScheme.secondary.light, 320, insideMargins = margins.verySmall.any.square,
		borderWidth = Some(4), stackMargin = margins.medium.downscaling,
		relatedItemsStackMargin = Some(margins.small.downscaling), switchWidth = Some(StackLength(32, 48, 64)),
		scrollBarWidth = 16, scrollBarIsInsideContent = true)
	
	// Creates the frame and displays it
	val actionLoop = new ActorLoop(actorHandler)
	implicit val context: ExecutionContext = new ThreadPool("ExportAndRun").executionContext
	
	val frame = Frame.windowed(new MainVC().view, "Export and Run", Program)
	frame.setToExitOnClose()
	
	actionLoop.registerToStopOnceJVMCloses()
	actionLoop.startAsync()
	StackHierarchyManager.startRevalidationLoop()
	frame.startEventGenerators(actorHandler)
	frame.isVisible = true
}
