package exp.controller

import java.nio.file.Path

import exp.model.logic.Operation
import exp.model.logic.OperationResult.{Completed, Output}

import scala.sys.process.{Process, ProcessLogger}

/**
 * Used for running operations
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
class Run(val command: String, targetDirectory: Path, val readOutput: Boolean = true) extends Operation
{
	override def description = s"Running '$command'${if (readOutput) "" else " (output ignored)"}"
	
	override def apply() =
	{
		// May read or ignore process output
		val builder = Process(command, targetDirectory.toFile)
		if (readOutput)
		{
			val output = new StringBuilder
			val logger = ProcessLogger(output append _)
			val status = builder ! logger
			Output(output.toString(), status == 0)
		}
		else
			Completed(builder.! == 0)
	}
}
