package domain

import io.circe.generic.JsonCodec

@JsonCodec
case class CreateUser(
   firstname: String,
   lastname: String,
   nickname: String,
   phone: String,
   password: String,
   )
