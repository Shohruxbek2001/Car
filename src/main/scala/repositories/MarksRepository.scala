package repositories

import cats.implicits.toFlatMapOps
import cats.implicits.toFunctorOps
import cats.effect.{Async, Resource}
import domain.{CreateMark, Mark}
import effects.{Calendar, GenUUID}
import org.typelevel.log4cats.Logger
import skunk.{*:, Session}

import java.util.UUID

trait UsersRepository[F[_]] {
  def create(createUser: CreateMark): F[Mark]

}

object UsersRepository {
  def make[F[_] : Async : Logger](
     implicit
     session: Resource[F, Session[F]]
   ): UsersRepository[F] = new UsersRepository[F] {
    import sql.MarksSql._

    override def create(createUser: CreateMark): F[Mark] =
      for {
        id <- GenUUID[F].make
        now <- Calendar[F].currentDateTime
        user = Mark(
          id = id,
          createdAt = now,
          firstName = createUser.firstname,
          lastName = createUser.lastname,
          nickName = createUser.nickname,
          phone = createUser.phone,
          password = createUser.password
        )
        _ <- session.use(
          _.execute(insert)(user)
        )
      } yield user

  }
}
