package domain

import io.circe.generic.JsonCodec

import java.time.LocalDateTime
import java.util.UUID

@JsonCodec
case class Car(
   id: UUID,
   createdAt: LocalDateTime,
   number: String,
   markId: UUID,
   colorId: UUID,
   yearId: UUID,
  )
@JsonCodec
case class CarTechnical(
    id: UUID,
    createdAt: LocalDateTime,
    number: String,
    mark: String,
    color: String,
    year: String,
  )
@JsonCodec
case class UpdateCar(
   id: UUID,
   number: String,
   markId: UUID,
   colorId: UUID,
   yearId: UUID,
   )