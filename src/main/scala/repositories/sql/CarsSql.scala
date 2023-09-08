package repositories.sql

import cats.effect._
import cats.implicits._
import domain.{Car, CarTechnical, CarsFilters, StatColor, StatMark, UpdateCar, YearsStatistics}
import skunk._
import skunk.codec.all._
import skunk.implicits._

import java.util.UUID


object CarsSql {

  implicit final class FragmentOps(af: AppliedFragment) {
    def whereAndOpt(fs: Option[AppliedFragment]*): AppliedFragment = {
      val filters =
        if (fs.flatten.isEmpty)
          AppliedFragment.empty
        else
          fs.flatten.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty)
      af |+| filters
    }
  }

  private val Columns =
    uuid *: timestamp *: varchar *: uuid *: uuid *: uuid

  val codec = Columns.to[Car]

  private val CarColumns =
    uuid *: timestamp *: varchar *: varchar *: varchar *: varchar

  val carCodec = CarColumns.to[CarTechnical]

  private val StatColumns =
    varchar *: int8

  val statCodec = StatColumns.to[YearsStatistics]
  val statByMarkCodec = StatColumns.to[StatMark]
  val statByColorCodec = StatColumns.to[StatColor]


  private def filtersCars(filters: CarsFilters): List[Option[AppliedFragment]] =
    List(
      filters.id.map(sql"cars.id = $uuid"),
      filters.mark.map(sql"marks.name = $varchar"),
      filters.number.map(sql"cars.number = $varchar"),
      filters.color.map(sql"colors.color = $varchar"),
      filters.year.map(sql"years.year = $varchar"),
    )

  private val UpdateCar =
    uuid *: varchar *: uuid *: uuid *: uuid

  val updateCodec = UpdateCar.to[UpdateCar]

  val insert: Command[Car] =
    sql"""INSERT INTO cars VALUES ($codec)""".command

  val getCars: Query[Void, CarTechnical] =
    sql"""SELECT cars.id, cars.created_at, cars.number, marks.name, colors.color, years.year FROM cars
          LEFT JOIN marks ON cars.mark_id = marks.id
          LEFT JOIN colors ON cars.color_id = colors.id
          LEFT JOIN years ON cars.year_id = years.id""".query(carCodec)

  val findCar: Query[String, Car] =
    sql"""SELECT * FROM cars WHERE number = $varchar""".query(codec)
  def select(filters: CarsFilters): AppliedFragment = {
    val filterCars: Fragment[Void] =
      sql"""SELECT cars.id, cars.created_at, cars.number, marks.name, colors.color, years.year FROM cars
            LEFT JOIN marks ON cars.mark_id = marks.id
            LEFT JOIN colors ON cars.color_id = colors.id
            LEFT JOIN years ON cars.year_id = years.id"""
    filterCars(Void).whereAndOpt(filtersCars(filters): _*) |+| sql"ORDER BY marks.name ASC".apply(Void)
  }

  val update: Command[UpdateCar] =
    sql"""UPDATE cars SET number = $varchar, mark_id = $uuid, color_id = $uuid, year_id = $uuid WHERE id = $uuid""".command
      .contramap { case uc: UpdateCar =>
        uc.number *: uc.markId *: uc.colorId *: uc.yearId *: uc.id *: EmptyTuple
      }

  val yearStatistics: Query[Void, YearsStatistics] =
    sql"""SELECT years.year, COUNT(cars.year_id) FROM cars INNER JOIN years ON cars.year_id = years.id GROUP BY years.year""".query(statCodec)

  val carsStatByMarks: Query[Void, StatMark] =
    sql"""SELECT marks.name, COUNT(cars.mark_id) FROM cars INNER JOIN marks ON cars.mark_id = marks.id GROUP BY marks.name""".query(statByMarkCodec)

  val carsStatByColors: Query[Void, StatColor] =
    sql"""SELECT colors.color, COUNT(cars.color_id) FROM cars INNER JOIN colors ON cars.color_id = colors.id GROUP BY colors.color""".query(statByColorCodec)

  val delete: Command[UUID] =
    sql"""DELETE FROM cars WHERE id = $uuid""".command
}
