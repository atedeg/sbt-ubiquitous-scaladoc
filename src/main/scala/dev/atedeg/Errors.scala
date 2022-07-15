package dev.atedeg

import better.files.File

sealed trait Error
final case class FileNotFound(lookupDir: File, path: String) extends Error {
  override def toString: String = s"Could not find file '$path' in directory '${lookupDir.pathAsString}'"
}
final case class ParseError(file: File, tag: String) extends Error {
  override def toString: String =
    s"Could not parse file '$file', missing tag '$tag'"
}
final case class AmbiguousName(name: String) extends Error {
  override def toString: String =
    s"More than one entity with the same name: '$name'"
}
final case class OverlappingIgnoredAndConsidered(overlapping: Set[IgnoredSelector]) extends Error {
  override def toString: String =
    s"One of the tables specified one or more entities that also appear in the ignored list: $overlapping"
}
final case class LeftoverEntities(leftovers: Set[IgnoredSelector]) extends Error {
  override def toString: String =
    s"There are one or more entities that are not considered nor ignored, maybe you forgot about those: $leftovers"
}
