package exp.model.logic

import java.nio.file.Path

/**
 * Links two paths, one from source and one from target
 * @author Mikko Hilpinen
 * @since 17.11.2019, v1+
 */
case class Link(from: Path, to: Path)
