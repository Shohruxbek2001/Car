import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits.catsSyntaxEitherId
import com.comcast.ip4s.IpLiteralSyntax
import domain.{CarTechnical, CarsFilters, CreateCar, CreateColor, CreateMark, CreateYear, UpdateCar, UpdateColor, UpdateMark, UpdateYear}
import natchez.Trace.Implicits.noop
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.{CarsRepository, ColorsRepository, MarksRepository, YearsRepository}
import skunk.Session
import skunk.codec.all.date
import skunk.implicits.toStringOps

object Main extends IOApp {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  implicit val session: Resource[IO, Session[IO]] =
    Session.single[IO]( // (2)
      host = "localhost",
      port = 5432,
      user = "workout",
      database = "chat",
      password = Some("123")
    )

  val markRepo = MarksRepository.make[IO]
  val colorRepo = ColorsRepository.make[IO]
  val yearRepo = YearsRepository.make[IO]
  val carRepo = CarsRepository.make[IO]


  object dsl extends Http4sDsl[IO]

  import dsl._

  val router = HttpRoutes.of[IO] {
    case req@POST -> Root / "create" / "mark" =>
      req.decode[CreateMark] { createMark =>
        (for {
          oldMark <- markRepo.findMark(createMark.name)
          res <- oldMark match {
            case Some(_) => BadRequest("Already is exists!")
            case None => markRepo.create(createMark).flatMap(Ok(_))
          }
        } yield res)
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest("Error while create year")
          }
      }

    case req@POST -> Root / "create" / "color" =>
      req.decode[CreateColor] { createColor =>
        (for {
          oldColor <- colorRepo.findColor(createColor.color)
          res <- oldColor match {
            case Some(_) => BadRequest("Already is exists!")
            case None => colorRepo.create(createColor).flatMap(Ok(_))
          }
        } yield res)
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest("Error while create year")
          }
      }

    case req@POST -> Root / "create" / "year" =>
      req.decode[CreateYear] { createYear =>
        (for {
          oldYear <- yearRepo.findYear(createYear.year)
          res <- oldYear match {
            case Some(_) => BadRequest("Already is exists!")
            case None => yearRepo.create(createYear).flatMap(Ok(_))
          }
        } yield res)
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest("Error while create year")
          }
      }

    case req@POST -> Root / "create" / "car" =>
      req.decode[CreateCar] { createCar =>
        (for {
          _ <- EitherT(carRepo.findCar(createCar.number).map(_.fold(().asRight[String])(_ => "Already is exists!".asLeft[Unit])))
          _ <- EitherT.fromOptionF(colorRepo.getColor(createCar.colorId), "Color is not exists!")
          _ <- EitherT.fromOptionF(markRepo.getMark(createCar.markId), "Mark is not exists!")
          _ <- EitherT.fromOptionF(yearRepo.getYear(createCar.yearId), "Year is not exists!")
          res <- EitherT.right[String](carRepo.create(createCar))
        } yield res).foldF(BadRequest(_), Ok(_))
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest()
          }
      }

    case req@POST -> Root / "search" / "cars" =>
      req.decode[CarsFilters] { carsFilters =>
        carRepo.getFilteredCars(carsFilters).flatMap(Ok(_))
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest()
          }
      }

    case req@PUT -> Root / "mark" =>
      req.decode[UpdateMark] { updateMark =>
        markRepo.update(updateMark).flatMap(Ok(_))
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest()
          }
      }

    case req@PUT -> Root / "year" =>
      req.decode[UpdateYear] { updateYear =>
        yearRepo.update(updateYear).flatMap(Ok(_))
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest()
          }
      }

    case req@PUT -> Root / "color" =>
      req.decode[UpdateColor] { updateColor =>
        colorRepo.update(updateColor).flatMap(Ok(_))
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest()
          }
      }

    case req@PUT -> Root / "car" =>
      req.decode[UpdateCar] { updateCar =>
        carRepo.update(updateCar).flatMap(Ok(_))
          .handleErrorWith { error =>
            logger.error(error)("Error message") >>
              BadRequest()
          }
      }

    case DELETE -> Root / "mark" / UUIDVar(id) =>
      markRepo.delete(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case DELETE -> Root / "year" / UUIDVar(id) =>
      yearRepo.delete(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case DELETE -> Root / "color" / UUIDVar(id) =>
      colorRepo.delete(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case DELETE -> Root / "car" / UUIDVar(id) =>
      carRepo.delete(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "marks" =>
      markRepo.getAllMark.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "cars" =>
      carRepo.getAllCars.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "colors" =>
      colorRepo.getAllColors.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "years" =>
      yearRepo.getAllYears.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "mark" / UUIDVar(id) =>
      markRepo.getMark(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "year" / UUIDVar(id) =>
      yearRepo.getYear(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "color" / UUIDVar(id) =>
      colorRepo.getColor(id).flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "statistics" / "year" =>
      carRepo.statByYears.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "statistics" / "color" =>
      carRepo.statByColors.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }

    case GET -> Root / "statistics" / "mark" =>
      carRepo.statByMarks.flatMap(Ok(_))
        .handleErrorWith { error =>
          logger.error(error)("Error message") >>
            BadRequest()
        }
  }
  val httpApp = Router("/" -> router).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .useForever
      .as(ExitCode.Success)
}