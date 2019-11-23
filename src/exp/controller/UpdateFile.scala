package exp.controller

import exp.model.logic.OperationResult.Completed
import utopia.flow.util.FileExtensions._
import exp.model.logic.{Link, Operation, OperationResult}

import scala.util.{Failure, Success}

/**
 * Updates a file to match another
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
class UpdateFile(val link: Link) extends Operation
{
	override def description = s"Updating ${link.to} to match ${link.from}"
	
	override def apply() =
	{
		// Source file must exist
		if (link.from.exists)
		{
			// If target file with same last edited time already exists, doesn't need to update the file
			if (link.to.exists && link.to.lastModified.toOption.exists { lastUpdated =>
				link.from.lastModified.toOption.contains(lastUpdated) })
				Completed()
			else
				link.to.overwriteWith(link.from) match
				{
					case Success(_) => Completed()
					case Failure(exception) => OperationResult.failure(exception.getMessage)
				}
		}
		else
			OperationResult.failure(s"${link.from} doesn't exist in the file system")
	}
}
