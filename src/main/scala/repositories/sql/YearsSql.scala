package repositories.sql

import domain.{UpdateYear, Year}
import skunk._
import skunk.codec.all._
import skunk.implicits._

import java.util.UUID

object YearsSql {
  private val Columns =
    uuid *: timestamp *: varchar

  val codec = Columns.to[Year]

  val insert: Command[Year] =
    sql"""INSERT INTO years VALUES ($codec)""".command

  val getAllYears: Query[Void, Year] =
    sql"""SELECT * FROM years ORDER BY year DESC""".query(codec)

  val getYear: Query[UUID, Year] =
    sql"""SELECT * FROM years WHERE id = $uuid LIMIT 1""".query(codec)

  val findYear: Query[String, Year] =
    sql"""SELECT * FROM years WHERE year = $varchar""".query(codec)

  val update: Command[UpdateYear] =
    sql"""UPDATE years SET year = $varchar WHERE id = $uuid""".command
      .contramap{case uy:UpdateYear =>
        uy.year *: uy.id *: EmptyTuple
      }

  val delete: Command[UUID] =
    sql"""DELETE FROM years WHERE id = $uuid""".command
}