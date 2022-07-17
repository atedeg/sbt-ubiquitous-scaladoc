package dev.atedeg

sealed trait EntityType {
  override def toString: String = EntityType.show(this)
}

object EntityType {
  private val typeToString: Map[EntityType, String] = Map(
    Class -> "class",
    Trait -> "trait",
    Enum -> "enum",
    Type -> "type",
    Case -> "case",
    Def -> "def",
  )
  private val stringToType: Map[String, EntityType] = typeToString.map(_.swap)
  def show(entityType: EntityType): String = typeToString.getOrElse(entityType, "")
  def read(s: String): Option[EntityType] = stringToType.get(s)
}
case object Class extends EntityType
case object Trait extends EntityType
case object Enum extends EntityType
case object Type extends EntityType
case object Case extends EntityType
case object Def extends EntityType

final case class Entity(entityType: EntityType, link: String, name: String, packageName: String) {
  def toBaseEntity: BaseEntity = BaseEntity(entityType, name)
  def sanitizedLink: String = link.split('#').head
  def entityId: Option[String] = link.split('#').lift(1)

  def isClassLike: Boolean = entityType match {
    case Class | Trait | Enum => true
    case Type | Case | Def => false
  }
}
final case class BaseEntity(entityType: EntityType, name: String)
final case class Configuration(ignored: Set[BaseEntity], tables: List[TableConfig])

final case class TableConfig(
    name: String,
    termName: Option[String],
    definitionName: Option[String],
    rows: List[BaseEntity],
)
