package llc.dodropin.common.config

import javax.inject.Singleton

import com.typesafe.config._
import com.typesafe.scalalogging.Logger

import scala.collection.JavaConverters._
import scala.util.Try

class ConfigurationException(message: String) extends Exception(message)
object ConfigurationException {
  def apply(path: String) = new ConfigurationException(
    s"Configuration path [$path] not defined."
  )
}

@Singleton
class Config() {
  private val log = Logger(this.getClass)

  private val conf = ConfigFactory.load()
  private val envVars = System.getenv().asScala
  private val params = System.getProperties().asScala

  private def pathAsEnvVar(path: String) = path.toUpperCase.replace(".", "_")

  private def pathFromEnv(path: String) =
    envVars.get(pathAsEnvVar(path)).map { v =>
      log.info(s"$path from env $v")
      v
    }

  private def pathFromParams(path: String) =
    params.get(pathAsEnvVar(path)).map { v =>
      log.info(s"$path from params $v")
      v
    }

  def getString(path: String): String =
    pathFromEnv(path)
      .orElse(pathFromParams(path))
      .getOrElse(conf.getString(path))

  def getStringList(path: String): List[String] =
    conf.getStringList(path).asScala.toList // TODO csv from env, params

  def getInt(path: String): Int =
    Try(getString(path).toInt).toOption
      .getOrElse(throw ConfigurationException(path))
}
