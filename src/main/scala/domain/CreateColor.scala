package domain

import io.circe.generic.JsonCodec

@JsonCodec
case class CreateColor(
    color: String,
    )