package repositories

import cats.effect.Async
import cats.effect.Resource
import domain.{Car, CarTechnical, CarsFilters, CreateCar, StatColor, StatMark, UpdateCar, YearsStatistics}
import effects.{Calendar, GenUUID}
import org.typelevel.log4cats.Logger
import skunk.Session
import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import repositories.sql.CarsSql

import java.util.UUID

trait CarsRepository[F[_]] {
  def create(createCar: CreateCar): F[Car]

  def getAllCars: F[List[CarTechnical]]

  def findCar(number: String): F[Option[Car]]

  def getFilteredCars(filters: CarsFilters): F[List[CarTechnical]]

  def update(characters: UpdateCar): F[Unit]

  def delete(carId: UUID): F[Unit]

  def statByYears: F[List[YearsStatistics]]
  def statByMarks: F[List[StatMark]]
  def statByColors: F[List[StatColor]]
}

object CarsRepository {
  def make[F[_] : Async : Logger](
                                   implicit
                                   session: Resource[F, Session[F]]
                                 ): CarsRepository[F] = new CarsRepository[F] {

    import sql.CarsSql._

    override def create(createCar: CreateCar): F[Car] =
      for {
        id <- GenUUID[F].make
        now <- Calendar[F].currentDateTime
        car = Car(
          id = id,
          createdAt = now,
          number = createCar.number,
          markId = createCar.markId,
          colorId = createCar.colorId,
          yearId = createCar.yearId
        )

        _ <- session.use(
          _.execute(insert)(car)
        )
      } yield car

    override def getAllCars: F[List[CarTechnical]] = {
      session.use {
        _.execute(getCars)
      }
    }

    override def getFilteredCars(filters: CarsFilters): F[List[CarTechnical]] = {
      session.use { a =>
        val query = select(filters)
        a.execute(query.fragment.query(carCodec))(query.argument)
      }
    }

    override def update(characters: UpdateCar): F[Unit] = {
      session.use {
        _.execute(CarsSql.update)(characters).void
      }
    }

    override def statByYears: F[List[YearsStatistics]] = {
      session.use {
        _.execute(CarsSql.yearStatistics)
      }
    }

    override def findCar(number: String): F[Option[Car]] = {
      session.use {
        _.option(CarsSql.findCar)(number)
      }
    }

    override def statByMarks: F[List[StatMark]] = {
      session.use {
        _.execute(CarsSql.carsStatByMarks)
      }
    }

    override def statByColors: F[List[StatColor]] = {
      session.use {
        _.execute(CarsSql.carsStatByColors)
      }
    }

    override def delete(carId: UUID): F[Unit] = {
      session.use {
        _.execute(CarsSql.delete)(carId).void
      }
    }
  }
}