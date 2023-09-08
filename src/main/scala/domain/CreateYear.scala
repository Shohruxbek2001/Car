package domain

import io.circe.generic.JsonCodec

@JsonCodec
case class CreateYear(
   year: String,
   )