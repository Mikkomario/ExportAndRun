package exp.controller

import java.nio.file.Path

import utopia.flow.util.StringExtensions._
import utopia.flow.util.CollectionExtensions._
import exp.model.logic.{ConfigException, ExportAndRun, Link, Operation}
import utopia.flow.util.FileExtensions._
import utopia.flow.datastructure.template.{Model, Property}
import utopia.flow.parse.JSONReader

import scala.util.{Failure, Success, Try}

/**
 * Reads configuration files and creates an operation sequence based on the file
 * @author Mikko Hilpinen
 * @since 22.11.2019, v1+
 */
object ConfigurationLoader
{
	/**
	 * Loads configurations from a configuration model
	 * @param configModel A model that contains configurations (under "target_name", "source_origin", "target_origin" and "start")
	 * @return A full operation sequence from parsed configuration
	 */
	def apply(configModel: Model[Property]) =
	{
		// Reads basic settings first
		val name = configModel("target_name").orElse(configModel("name")).stringOr("Operation")
		val source: Path = configModel("source_origin").orElse(configModel("source")).string.map { s => s: Path }.getOrElse(".")
		val target: Path = configModel("target_origin").orElse(configModel("target")).string.map { s => s: Path }.getOrElse(".")
		
		if (source == target)
			Failure(new ConfigException("Please specify different source_origin and target_origin"))
		else
		{
			val operations: Try[Vector[Operation]] = configModel("start").getVector.flatMap { _.model }.tryMap { opModel =>
				if (opModel.contains("run"))
				{
					opModel("run").getString.notEmpty match
					{
						case Some(command) => Success(new RunAsync(command, target, opModel("asynchronously").booleanOr(true)))
						case None => Failure(new ConfigException("'run' must not be empty or null"))
					}
				}
				else if (opModel.contains("update"))
				{
					opModel("update").getString.notEmpty match
					{
						case Some(targetFileName) =>
							val targetFile = target/targetFileName
							val sourceFile = opModel("to_match").string.map { source/_ }.getOrElse { source/targetFileName }
							Success(new UpdateFile(Link(sourceFile, targetFile)))
						case None => Failure(new ConfigException("'update' must not be empty or null"))
					}
				}
				else
					Failure(new ConfigException(s"Unrecognized operation: $opModel"))
			}
			
			operations.map { ExportAndRun(name, _) }
		}
	}
	
	/**
	 * Loads configurations from a specific configuration json file
	 * @param jsonFile A path leading to the configuration file
	 * @return A full operation sequence parsed from file contents
	 */
	def apply(jsonFile: Path): Try[ExportAndRun] = JSONReader(jsonFile.toFile).flatMap { _.model match
	{
		case Some(model) => apply(model)
		case None => Failure(new ConfigException(s"Configuration file $jsonFile doesn't start with a json object"))
	}}
}
