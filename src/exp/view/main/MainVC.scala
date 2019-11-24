package exp.view.main

import java.nio.file.Path

import exp.controller.{ConfigurationLoader, SavedSettings}
import exp.model.logic.ExportAndRun
import exp.model.ui.{ColorScheme, Margins}
import utopia.reflection.container.stack.SingleStackContainer
import utopia.reflection.container.{Container, SingleContainer}
import utopia.reflection.container.swing.Stack.AwtStackable
import utopia.reflection.container.swing.{AwtContainerRelated, SwitchPanel}
import utopia.reflection.localization.Localizer
import utopia.reflection.util.ComponentContextBuilder

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Main view controller for this program. Handles transitions between phases.
 * @author Mikko Hilpinen
 * @since 24.11.2019, v1+
 */
class MainVC(implicit baseCB: ComponentContextBuilder, defaultLangCode: String, localizer: Localizer,
			 colorScheme: ColorScheme, margins: Margins, exc: ExecutionContext)
{
	// ATTRIBUTES	-----------------------
	
	private var mainPanel: Option[SwitchPanel[AwtStackable]] = None
	
	
	// INITIAL CODE	----------------------
	
	// Starts by either reading or requesting config file location
	SavedSettings.configDir match
	{
		case Some(dir) => configLocationSelected(dir)
		case None => show(new RequestSettingsVC(c =>
		{
			SavedSettings.configDir = c
			configLocationSelected(c)
		}).view)
	}
	
	
	// COMPUTED	--------------------------
	
	/**
	 * @return View handled by this VC
	 */
	// Should always be initialized at this point
	def view: SingleStackContainer[_ <: AwtStackable] with AwtContainerRelated = mainPanel
		.getOrElse(new ErrorVC("Failed to load main view").view)
	
	
	// OTHER	---------------------------
	
	private def show(newView: AwtStackable) = mainPanel match
	{
		case Some(panel) => panel.set(newView)
		case None => mainPanel = Some(new SwitchPanel(newView))
	}
	
	private def showError(message: String) = show(new ErrorVC(message).view)
	
	private def configLocationSelected(locationDir: Path) =
	{
		// Once configuration location has been selected, loads condifuration data (may fail)
		ConfigurationLoader.readAllFrom(locationDir) match
		{
			case Success(configs) =>
				// If no configurations were read, informs the user
				if (configs.isEmpty)
					showError(s"No configurations (.json) were read from $locationDir. Please add configurations and try again.")
				// If multiple configurations were read, lets the user choose
				else if (configs.size > 1)
					show(new SelectConfigurationVC(configs, configurationSelected).view)
				// Otherwise continues with the only read configuration
				else
					configurationSelected(configs.head)
				
			case Failure(error) =>
				error.printStackTrace()
				showError(s"Failed to read configuration(s). Error message: ${error.getMessage}")
		}
	}
	
	private def configurationSelected(selectedConfig: ExportAndRun) = show(new RunVC(selectedConfig).view)
}
