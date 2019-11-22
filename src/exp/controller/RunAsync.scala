package exp.controller

import java.nio.file.Path

import exp.model.logic.Operation
import exp.model.logic.OperationResult.OutputStream
import utopia.flow.datastructure.mutable.PointerWithEvents

import scala.sys.process.{Process, ProcessLogger}

/**
 * Performs an external process asynchronously
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
// TODO: At some point you may wish to take user input. See: https://stackoverflow.com/questions/53557874/write-to-process-stdin
// TODO: Or maybe a simple shutdown command like mysqld stop or something
class RunAsync(val command: String, targetDirectory: Path, val shouldBlockNextOperation: Boolean = false) extends Operation
{
	override def description = s"Running '$command'${if (shouldBlockNextOperation) "" else " in background"}"
	
	override def apply() =
	{
		val pointer = new PointerWithEvents("")
		val logger = ProcessLogger { s => pointer.set(s) }
		val outputStream = Process(command, targetDirectory.toFile).lineStream(logger)
		OutputStream(outputStream, pointer, shouldBlockNextOperation)
	}
}
