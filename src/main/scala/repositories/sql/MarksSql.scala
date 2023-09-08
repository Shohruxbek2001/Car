package repositories.sql

import domain.User
import skunk._
import skunk.codec.all._
import skunk.implicits._
object UsersSql {
  private val Columns =
    uuid *: timestamp *: varchar *: varchar *: varchar *: varchar *: varchar

  val codec = Columns.to[User]

  val insert: Command[User] =
    sql"""INSERT INTO users VALUES ($codec)""".command
}
