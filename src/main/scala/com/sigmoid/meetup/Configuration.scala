package com.sigmoid.meetup

import com.typesafe.config.ConfigFactory

/**
 * Created by ajay on 16/10/15.
 */
trait Configuration {

  val configFactory = ConfigFactory.load()
  val appConfig = configFactory.getConfig("com.sigmoid.meetup.app")
  val sparkConfig = configFactory.getConfig("com.sigmoid.meetup.spark")
  val sprayConfig = configFactory.getConfig("com.sigmoid.meetup.spray")
  val csvConfig = configFactory.getConfig("com.sigmoid.meetup.csv")

  protected val name = sparkConfig.getString("name")
  protected val master = sparkConfig.getString("master")
  protected val interface = sprayConfig.getString("interface")
  protected val port = sprayConfig.getInt("port")
  protected val timeoutDelay = sprayConfig.getInt("timeout")
  protected val data = getClass.getResource(csvConfig.getString("data")).getPath
  protected val format = csvConfig.getString("format")
  protected val output = appConfig.getString("output")

  val users: Seq[User] = Seq(
    User("ajay", Some("viswanathan"), "admin")
  )

  def permissions(role: String): Set[String] = role match {
    case "developer" => Set("GET")
    case "admin" => Set("GET", "POST", "PUT", "DELETE")
    case _ => Set()
  }
}
