package domain

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class CarsFilters(
  id: Option[UUID] = None,
  number: Option[String] = None,
  mark: Option[String] = None,
  color: Option[String] = None,
  year: Option[String] = None,
  )

object CarsFilters {
  val Empty: CarsFilters = CarsFilters()
}