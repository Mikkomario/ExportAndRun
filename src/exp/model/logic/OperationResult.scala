package exp.model.logic

import utopia.flow.event.Changing

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Contains results for a runnable operation
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
trait OperationResult
{
	def wasSuccess: Boolean
}

object OperationResult
{
	/**
	 * Operation completed without output
	 * @param wasSuccess Whether operation was a success (default = true)
	 */
	case class Completed(wasSuccess: Boolean = true) extends OperationResult
	
	/**
	 * Operation completed with output
	 * @param out Output as a string
	 * @param wasSuccess Whether operation was a success (default = true)
	 */
	case class Output(out: String, wasSuccess: Boolean = true) extends OperationResult
	
	/**
	 * Operation completing or completed in background
	 * @param stream Output lines stream
	 * @param lastErrorPointer Pointer that always contains a link to the latest error output line
	 * @param shouldWaitForCompletion Whether operation should be finished before starting any other operations
	 *                                (default = false)
	 */
	case class OutputStream(private val stream: Stream[String], private val lastErrorPointer: Changing[String],
							shouldWaitForCompletion: Boolean = false) extends OperationResult
	{
		override lazy val wasSuccess = Try { stream.foreach { _ => Unit } }.isSuccess
		
		/**
		 * Performs an operation for all the lines in this stream. Operation is performed in current thread and
		 * blocks until this whole operation has been completed. This may take an extended period of time.
		 * @param f A function called for each new line
		 * @return Result of the process once finished. Contains either a success or a failure, based on
		 *         final operation status
		 */
		def forLines(f: String => Unit) =
		{
			// Provided function will also be called for std.err
			lastErrorPointer.addListener { e => f(e.newValue) }
			Try { stream.foreach(f) }
		}
		
		/**
		 * Performs an operation for all the lines in this stream. Operating is done asynchronously, since reading
		 * output lines may block for extended periods of time
		 * @param f A function called for each new line
		 * @param exc Implicit execution context
		 * @return Asynchronous completion of the process, which contains either a success or a failure, based on
		 *         final operation status
		 */
		def forLinesAsync(f: String => Unit)(implicit exc: ExecutionContext) =
		{
			// Provided function will also be called for std.err
			lastErrorPointer.addListener { e => f(e.newValue) }
			Future { Try { stream.foreach(f) } }
		}
	}
	
	/**
	 * @param message Message for failure
	 * @return A failure result with message as output
	 */
	def failure(message: String) = Output(message, wasSuccess = false)
}