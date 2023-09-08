package domain

import io.circe.generic.JsonCodec

import java.time.LocalDateTime
import java.util.UUID

@JsonCodec
case class Color(
    id: UUID,
    createdAt: LocalDateTime,
    color: String,
    )

@JsonCodec
case class UpdateColor(
    id: UUID,
    color: String,
    )

@JsonCodec
case class StatColor(
    color: String,
    cars: Long,
    )