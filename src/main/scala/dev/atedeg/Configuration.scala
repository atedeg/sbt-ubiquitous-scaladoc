package dev.atedeg

sealed trait EntityType {

  override def toString: String = this match {
    case Class => "class"
    case Trait => "trait"
    case Enum => "enum"
    case Type => "type"
    case Case => "case"
    case Def => "def"
  }
}

object EntityType {

  def fromString(s: String): Option[EntityType] = s match {
    case "class" => Some(Class)
    case "trait" => Some(Trait)
    case "enum" => Some(Enum)
    case "type" => Some(Type)
    case "case" => Some(Case)
    case "def" => Some(Def)
    case _ => None
  }
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
