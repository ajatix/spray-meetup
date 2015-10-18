package com.sigmoid.meetup

import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.directives.AuthMagnet

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by ajay on 17/10/15.
 */
trait Authenticator extends Configuration {
  /**
   * More info on magnet pattern: http://spray.io/blog/2012-12-13-the-magnet-pattern/
   * @param ec
   * @return
   */
  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[AuthInfo] = {
    def validateUser(userPass: Option[UserPass]): Option[AuthInfo] = {
      for {
        p <- userPass
        user <- users.find(user => p.user == user.login)
        if user.passwordMatches(p.pass)
      } yield new AuthInfo(user)
    }

    def authenticator(userPass: Option[UserPass]): Future[Option[AuthInfo]] = Future {
      validateUser(userPass)
    }

    BasicAuth(authenticator _, realm = "Private API")
  }

}

case class User(login: String, hashedPassword: Option[String] = None, role: String) {

  def withPassword(password: String): User = copy(hashedPassword = Some(password))
  def passwordMatches(password: String): Boolean = hashedPassword.exists(hp => password == hp)

}

case class Authenticate(user: User, allowed: Set[String])

class AuthInfo(val user: User) extends Configuration {
  def hasPermission(permission: String): Boolean =
    permissions(user.role).contains(permission)
}