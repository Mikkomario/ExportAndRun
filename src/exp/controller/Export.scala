package exp.controller

import java.nio.file.Path

import exp.model.logic.OperationResult.Completed
import utopia.flow.util.StringExtensions._
import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.FileExtensions._
import exp.model.logic.{Link, Operation, OperationResult}

import scala.util.{Failure, Success, Try}

/**
 * Exports contents of a source directory to target
 * @author Mikko Hilpinen
 * @since 23.11.2019, v1+
 */
class Export(val link: Link) extends Operation
{
	override def description = s"Exports ${link.from} to ${link.to}"
	
	override def apply() =
	{
		// Source must exist
		if (link.from.exists)
		{
			val exportResult: Try[Any] =
			{
				// In case of a directory export, updates all files to target directory
				if (link.from.isDirectory)
				{
					link.to.createDirectories().flatMap { targetDir =>
						// Skips files that already exist in target
						targetDir.children.flatMap { existingChildren =>
							link.from.children.flatMap { filesToExport =>
								filesToExport.tryForEach { fileToExport =>
									// If there already exists a file with same name, either overwrites it or does nothing
									// (if same last modified time)
									// If there isn't such a file in target dir already, simply copies the exported file
									existingChildren.find { _.fileName ~== fileToExport.fileName }.map {
										_.overwriteWithIfChanged(fileToExport) }
										.getOrElse { fileToExport.copyTo(targetDir) }.map { _ => Unit }
								}
							}
						}
					}
				}
				// In case of a regular file, either copies it to target directory or overwrites target with it
				// (if target is not a directory)
				else
				{
					// May need to create target directories
					link.to.createDirectories().flatMap { target =>
						val fileToExport = link.from
						val sourceFileName = fileToExport.fileName
						if (target.isDirectory)
						{
							// Finds an existing file to overwrite or simply copies target file to directory
							target.children.flatMap { _.find { _.fileName ~== sourceFileName }
								.map { _.overwriteWithIfChanged(fileToExport) }.getOrElse { fileToExport.copyTo(target) } }
						}
						else
						{
							// In case target is a regular file, overwrites it with exported file (if changed)
							target.overwriteWithIfChanged(fileToExport)
						}
					}
				}
			}
			
			exportResult match
			{
				case Success(_) => Completed()
				case Failure(ex) => OperationResult.failure(ex.getMessage)
			}
		}
		else
			OperationResult.failure(s"${link.from} doesn't exist in file system")
	}
}
