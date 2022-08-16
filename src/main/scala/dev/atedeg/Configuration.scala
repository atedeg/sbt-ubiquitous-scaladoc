package dev.atedeg

import cats.{ Eq, Order }
import cats.data.{ NonEmptyList, NonEmptyMap }

sealed trait EntityType {
  override def toString: String = EntityType.show(this)
}
case object Class extends EntityType
case object Trait extends EntityType
case object Enum extends EntityType
case object Type extends EntityType
case object Case extends EntityType
case object Def extends EntityType

object EntityType {
  implicit val ordEntityType: Order[EntityType] = Order.from(_.hashCode - _.hashCode)

  private val typeToString = NonEmptyMap.of[EntityType, String](
    Class -> "class",
    Trait -> "trait",
    Enum -> "enum",
    Type -> "type",
    Case -> "case",
    Def -> "def",
  )
  private val stringToType = typeToString.mapBoth((k, v) => (v, k))
  def cases: NonEmptyList[EntityType] = typeToString.keys.toNonEmptyList
  def show(entityType: EntityType): String = typeToString(entityType).getOrElse("")
  def read(s: String): Option[EntityType] = stringToType(s)
}

final case class Entity(entityType: EntityType, link: String, name: String, packageName: String) {
  def toBaseEntity: BaseEntity = BaseEntity(entityType, name)
  def sanitizedLink: String = link.split('#').head
  def entityId: Option[String] = link.split('#').lift(1)
  def fullyQualifiedName: String = packageName.replace("/", ".").replace("$$", ".")
}

final case class BaseEntity(entityType: EntityType, name: String) {
  override def toString: String = s"$entityType: $name"
}

final case class NamedBaseEntity(entityType: EntityType, name: String, wantedName: Option[String]) {
  def toBaseEntity: BaseEntity = BaseEntity(entityType, name)
}

final case class Configuration(ignored: Set[BaseEntity], tables: List[TableConfig])

final case class TableConfig(
    name: String,
    termName: Option[String],
    definitionName: Option[String],
    rows: List[NamedBaseEntity],
)

object BaseEntity {
  implicit val eqBaseEntity: Eq[BaseEntity] = Eq.fromUniversalEquals[BaseEntity]
}
