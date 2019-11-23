package exp.controller

import utopia.flow.util.CollectionExtensions._
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.FileExtensions._
import java.nio.file.Path

import utopia.flow.datastructure.immutable.Model
import utopia.flow.parse.JSONReader

/**
 * Handles settings loading and saving between sessions
 * @author Mikko Hilpinen
 * @since 23.11.2019, v1+
 */
object SavedSettings
{
	private val settingsFile: Path = "settings.json"
	
	private var isLoaded = false
	private var _configDir: Option[Path] = None
	
	/**
	 * @return Directory where configurations are loaded from (may be empty if not yet specified)
	 */
	def configDir =
	{
		if (!isLoaded)
			load()
		_configDir
	}
	
	/**
	 * Specifies a configuration directory
	 * @param directory A directory where configuration files are read from
	 */
	def configDir_=(directory: Path) =
	{
		isLoaded = true
		_configDir = Some(directory)
		// Saves changed settings (if possible)
		save().failure.foreach { _.printStackTrace() }
	}
	
	private def save() = settingsFile.writeJSON(Model(Vector("config_directory" -> _configDir.map { _.toString })))
	
	private def load() =
	{
		JSONReader(settingsFile.toFile).toOption.flatMap { _.model }.foreach { model =>
			_configDir = model("config_directory").string.map { s => s: Path }
		}
		isLoaded = true
	}
}
