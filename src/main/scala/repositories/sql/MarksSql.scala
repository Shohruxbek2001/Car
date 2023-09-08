package repositories.sql

import domain.{Mark, UpdateMark}
import skunk._
import skunk.codec.all._
import skunk.implicits._

import java.util.UUID

object MarksSql {
  private val Columns =
    uuid *: timestamp *: varchar

  val codec = Columns.to[Mark]

  val insert: Command[Mark] =
    sql"""INSERT INTO marks VALUES ($codec)""".command

  val getAllMarks: Query[Void, Mark] =
    sql"""SELECT * FROM marks ORDER BY name ASC""".query(codec)

  val getMark: Query[UUID, Mark] =
    sql"""SELECT * FROM marks WHERE id = $uuid LIMIT 1""".query(codec)

  val findMark: Query[String, Mark] =
    sql"""SELECT * FROM marks WHERE name = $varchar""".query(codec)

  val update: Command[UpdateMark] = {
    sql"""UPDATE marks SET name = $varchar WHERE id = $uuid""".command
      .contramap{case um:UpdateMark =>
          um.name *: um.id *: EmptyTuple
      }
  }

  val delete: Command[UUID] = {
    sql"""DELETE FROM marks WHERE id = $uuid""".command
  }
}
