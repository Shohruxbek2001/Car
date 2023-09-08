package repositories

import cats.effect.{Async, Resource}
import cats.implicits.{toFlatMapOps, toFunctorOps}
import domain.{CreateMark, Mark, UpdateMark}
import effects.{Calendar, GenUUID}
import org.typelevel.log4cats.Logger
import repositories.sql.MarksSql
import skunk.Session
import skunk.data.Completion

import java.util.UUID

trait MarksRepository[F[_]] {
  def create(createMark: CreateMark): F[Mark]

  def getAllMark: F[List[Mark]]

  def getMark(markId: UUID): F[Option[Mark]]
  def findMark(mark: String): F[Option[Mark]]
  def update(updateMark: UpdateMark): F[Unit]

  def delete(markId: UUID): F[Unit]

}

object MarksRepository {
  def make[F[_] : Async : Logger](
                                   implicit
                                   session: Resource[F, Session[F]]
                                 ): MarksRepository[F] = new MarksRepository[F] {

    import sql.MarksSql._

    override def create(createMark: CreateMark): F[Mark] =
      for {
        id <- GenUUID[F].make
        now <- Calendar[F].currentDateTime
        mark = Mark(
          id = id,
          createdAt = now,
          name = createMark.name,
        )
        _ <- session.use(
          _.execute(insert)(mark)
        )
      } yield mark

    override def getAllMark: F[List[Mark]] = {
      session.use {
        _.execute(getAllMarks)
      }
    }

    override def getMark(markId: UUID): F[Option[Mark]] = {
      session.use {
        _.option(MarksSql.getMark)(markId)
      }
    }

    override def findMark(mark: String): F[Option[Mark]] = {
      session.use {
        _.option(MarksSql.findMark)(mark)
      }
    }

    override def update(updateMark: UpdateMark): F[Unit] = {
      session.use {
        _.execute(MarksSql.update)(updateMark).void
      }
    }

    override def delete(markId: UUID): F[Unit] = {
      session.use {
        _.execute(MarksSql.delete)(markId).void
      }
    }


  }
}
