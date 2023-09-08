package repositories

import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import cats.effect.{Async, Resource}
import domain.{Color, CreateColor, UpdateColor}
import effects.{Calendar, GenUUID}
import org.typelevel.log4cats.Logger
import repositories.sql.ColorsSql
import skunk.Session

import java.util.UUID

trait ColorsRepository[F[_]] {
  def create(createColor: CreateColor): F[Color]
  def getAllColors: F[List[Color]]

  def getColor(colorId: UUID): F[Option[Color]]
  def findColor(color: String): F[Option[Color]]

  def update(updateColor: UpdateColor): F[Unit]
  def delete(colorId: UUID): F[Unit]
}

object ColorsRepository {
  def make[F[_] : Async : Logger](
   implicit
   session: Resource[F, Session[F]]
   ): ColorsRepository[F] = new ColorsRepository[F] {
    import sql.ColorsSql._

    override def create(createColor: CreateColor): F[Color] =
      for {
        id <- GenUUID[F].make
        now <- Calendar[F].currentDateTime
        color = Color(
          id = id,
          createdAt = now,
          color = createColor.color
        )

        _ <- session.use(
          _.execute(insert)(color)
        )
      } yield color

    override def getAllColors: F[List[Color]] = {
      session.use {
        _.execute(getColors)
      }
    }

    override def getColor(colorId: UUID): F[Option[Color]] = {
      session.use {
        _.option(color)(colorId)
      }
    }

    override def findColor(color: String): F[Option[Color]] = {
      session.use {
        _.option(ColorsSql.findColor)(color)
      }
    }

    override def update(updateColor: UpdateColor): F[Unit] = {
      session.use {
        _.execute(ColorsSql.update)(updateColor).void
      }
    }

    override def delete(colorId: UUID): F[Unit] = {
      session.use {
        _.execute(ColorsSql.delete)(colorId).void
      }
    }
  }
}