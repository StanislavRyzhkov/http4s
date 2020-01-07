package company.ryzhkov.util

import java.util.{Base64, Collections, Date}

import cats.effect.IO
import company.ryzhkov.util.Constants.AccessDenied
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}

object TokenProvider {
  val secret: String =
    Base64.getEncoder
      .encodeToString(
        "dyh32&n(qDC%!#DRV[&^5rgfdRVb790&^FGDswd341".getBytes()
      )

  val expired = 2500000000L

  def createToken(username: String): IO[String] = IO {
    val roles = Collections.singletonList("ROLE_USER")
    val claims = Jwts.claims.setSubject(username)
    claims.put("roles", roles)
    val now = new Date()
    val validity = new Date(now.getTime + expired)
    Jwts.builder
      .setClaims(claims)
      .setIssuedAt(now)
      .setExpiration(validity)
      .signWith(SignatureAlgorithm.HS256, secret)
      .compact
  }

  def getUsername(optionHeader: Option[String]): IO[String] =
    IO {
      optionHeader match {
        case Some(header) =>
          header.split(" ") match {
            case Array("Bearer", token) =>
              try {
                Jwts.parser
                  .setSigningKey(secret)
                  .parseClaimsJws(token)
                  .getBody
                  .getSubject
              } catch {
                case _: Exception => throw new Exception(AccessDenied)
              }
            case _ => throw new Exception(AccessDenied)
          }
        case None => throw new Exception(AccessDenied)
      }
    }
}
