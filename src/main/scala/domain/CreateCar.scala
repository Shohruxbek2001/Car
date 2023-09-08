package domain

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class CreateCar(
  number: String,
  markId: UUID,
  colorId: UUID,
  yearId: UUID,
  )