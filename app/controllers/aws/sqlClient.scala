package src.main.scala

import java.sql.{PreparedStatement, Connection, DriverManager}
import model.Photo

import scala.collection.immutable.HashMap
import scala.util.control.NonFatal

/**
 * Created by pdesai on 10/11/15.
 */
object sqlClient {

  var connection: Connection = null
  var preparedStmt = HashMap[String, PreparedStatement]()

  def init(): Unit = {
    val driver = "com.mysql.jdbc.Driver"
    val url = ""
    val username = ""
    val password = ""

    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    preparedStmt += "insertIntoTable" -> connection.prepareStatement("INSERT INTO newsfeed (caption, url, mediaType) VALUES (?, ?, ?)")

  }

  def insertIntoTable(photo: Photo) : Either[String, String]= {
    val func = "insertIntoTable"
    try {
      val stmt = preparedStmt.get("insertIntoTable").get
      stmt.setString(1, s"${photo.caption}")
      stmt.setString(2, s"${photo.url}")
      stmt.setString(3, s"${photo.mediaType}")
      stmt.execute()
      Right("Success")
    } catch{
      case NonFatal(exc) =>
        println(s"$func: ${exc.toString}")
        Left(exc.toString)
    }
  }

  def close(): Unit = {
    connection.close()
  }
}
