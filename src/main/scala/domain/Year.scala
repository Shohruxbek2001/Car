package domain

import io.circe.generic.JsonCodec

import java.time.LocalDateTime
import java.util.UUID

@JsonCodec
case class Year(
   id: UUID,
   createdAt: LocalDateTime,
   year: String
   )

@JsonCodec
case class UpdateYear(
   id: UUID,
   year: String
   )

@JsonCodec
case class YearsStatistics(
   year: String,
   cars: Long,
   )