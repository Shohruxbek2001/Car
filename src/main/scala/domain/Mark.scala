package domain

import io.circe.generic.JsonCodec

import java.time.LocalDateTime
import java.util.UUID

@JsonCodec
case class User(
   id: UUID,
   createdAt: LocalDateTime,
   firstName: String,
   lastName: String,
   nickName: String,
   phone: String,
   password: String,
   )
