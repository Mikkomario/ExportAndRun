package exp.model.logic

import exp.model.logic.OperationResult.{Completed, Output, OutputStream}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Contains all operations required in program export & running
 * @author Mikko Hilpinen
 * @since 22.11.2019, v1+
 */
case class ExportAndRun(name: String, startTasks: Vector[Operation])
{
	/**
	 * Performs all tasks that are part of this operation sequence
	 * @param handleOutput A function that uses / accepts process output (lines of text)
	 * @param exc Implicit execution context for performing some tasks asynchronously
	 * @return Whether all (synchronous) operations were successfully completed +
	 *         eventual completions of any asynchronous processes
	 */
	def apply(handleOutput: String => Unit)(implicit exc: ExecutionContext) =
	{
		// Performs all operations in sequence (some may be done in background)
		// Stops if any operation fails
		var asyncCompletions = Vector[Future[Try[Unit]]]()
		val wasSuccess = !startTasks.exists { task =>
			// Outputs a description of each task before it is run
			handleOutput(task.description)
			task() match
			{
				case completion: Completed => !completion.wasSuccess
				case output: Output =>
					handleOutput(output.out)
					!output.wasSuccess
				case stream: OutputStream =>
					// Will not wait asynchronous operations to finnish
					if (stream.shouldWaitForCompletion)
						stream.forLines(handleOutput).isFailure // TODO: Handle error?
					else
					{
						asyncCompletions :+= stream.forLinesAsync(handleOutput)
						false
					}
			}
		}
		
		// Returns success/failure status and asynchronous operation completions
		wasSuccess -> asyncCompletions
	}
}
