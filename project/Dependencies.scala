import Libs.*
import sbt.*

import scala.collection.Seq

trait Dependencies {
  def dependencies: Seq[ModuleID]
}

object Dependencies {

  object Auth extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(zio, pureconfig).flatten
  }

  object Routing extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(zio, pureconfig).flatten
  }

  object Helper extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(zio, pureconfig).flatten
  }

  object Repository extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(zio, pureconfig, flyway, circe, pdiJwt, postgres).flatten
  }

  object MapRepository extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(zio, pureconfig, flyway, circe, pdiJwt, postgres, locationtechjts).flatten
  }
}