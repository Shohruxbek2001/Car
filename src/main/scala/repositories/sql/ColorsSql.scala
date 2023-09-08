package repositories.sql

import domain.{Color, UpdateColor}
import skunk._
import skunk.codec.all._
import skunk.implicits._

import java.util.UUID

object ColorsSql {
  private val Columns =
    uuid *: timestamp *: varchar

  val codec = Columns.to[Color]

  val insert: Command[Color] =
    sql"""INSERT INTO colors VALUES ($codec)""".command

  val getColors: Query[Void, Color] =
    sql"""SELECT * FROM colors ORDER BY color ASC""".query(codec)

  val color: Query[UUID, Color] =
    sql"""SELECT * FROM colors WHERE id = $uuid limit 1""".query(codec)

  val findColor: Query[String, Color] =
    sql"""SELECT * FROM colors WHERE color = $varchar""".query(codec)

  val update: Command[UpdateColor] =
    sql"""UPDATE colors SET color = $varchar WHERE id = $uuid""".command
      .contramap{case uc:UpdateColor =>
        uc.color *: uc.id *: EmptyTuple
      }

  val delete: Command[UUID] =
    sql"""DELETE FROM colors WHERE id = $uuid""".command
}