package domain

import io.circe.generic.JsonCodec

import java.time.LocalDateTime
import java.util.UUID

@JsonCodec
case class Mark(
   id: UUID,
   createdAt: LocalDateTime,
   name: String,
   )

@JsonCodec
case class UpdateMark(
   id: UUID,
   name: String,
   )

@JsonCodec
case class StatMark(
   mark: String,
   cars: Long,
   )