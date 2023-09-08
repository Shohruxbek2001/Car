package repositories

import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import cats.effect.{Async, Resource}
import domain.{CreateYear, UpdateYear, Year}
import effects.{Calendar, GenUUID}
import org.typelevel.log4cats.Logger
import repositories.sql.YearsSql
import skunk.Session

import java.util.UUID

trait YearsRepository[F[_]] {
  def create(createYear: CreateYear): F[Year]

  def getAllYears: F[List[Year]]

  def getYear(yearId: UUID): F[Option[Year]]
  def findYear(year: String): F[Option[Year]]

  def update(updateYear: UpdateYear): F[Unit]

  def delete(yearId: UUID): F[Unit]
}

object YearsRepository {
  def make[F[_] : Async : Logger](
      implicit
      session: Resource[F, Session[F]]
     ): YearsRepository[F] = new YearsRepository[F] {
    import sql.YearsSql._

    override def create(createYear: CreateYear): F[Year] =
      for {
        id <- GenUUID[F].make
        now <- Calendar[F].currentDateTime
        year = Year(
          id = id,
          createdAt = now,
          year = createYear.year,
        )
        _ <- session.use(
          _.execute(insert)(year)
        )
      } yield year

    override def getAllYears: F[List[Year]] = {
      session.use {
        _.execute(YearsSql.getAllYears)
      }
    }

    override def getYear(yearId: UUID): F[Option[Year]] = {
      session.use {
        _.option(YearsSql.getYear)(yearId)
      }
    }

    override def findYear(year: String): F[Option[Year]] = {
      session.use {
        _.option(YearsSql.findYear)(year)
      }
    }

    override def update(updateYear: UpdateYear): F[Unit] = {
      session.use {
        _.execute(YearsSql.update)(updateYear).void
      }
    }

    override def delete(yearId: UUID): F[Unit] = {
      session.use {
        _.execute(YearsSql.delete)(yearId).void
      }
    }

  }


}