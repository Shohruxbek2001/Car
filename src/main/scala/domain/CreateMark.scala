package domain

import io.circe.generic.JsonCodec

@JsonCodec
case class CreateMark(
   name: String,
   )
