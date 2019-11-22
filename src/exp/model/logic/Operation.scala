package exp.model.logic

/**
 * Used for performing certain actions that may produce a result
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
trait Operation
{
	/**
	 * @return A text representation of what this operation does
	 */
	def description: String
	
	/**
	 * Performs this operation
	 * @return Results of this operation
	 */
	def apply(): OperationResult
}
