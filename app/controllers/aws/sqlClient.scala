package src.main.scala

import java.sql.{PreparedStatement, Connection, DriverManager}
import model.Photo

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal

/**
 * Created by pdesai on 10/11/15.
 */
object SQLClient {

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
    preparedStmt += "listPhotos" -> connection.prepareStatement("SELECT * FROM newsfeed")
  }

  def insertPhoto(photo: Photo) : Either[String, String]= {
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
  
  def listPhotos = {
    val func = "listPhotos"
    val truckList = new ListBuffer[Photo]

    try {
      val stmt = preparedStmt.get("listPhotos").get
      val rs = stmt.executeQuery()
      while (rs.next()) {
        truckList += Photo(rs.getInt("id"),
          rs.getString("caption"),
          rs.getString("url"),
          rs.getString("mediaType"))
      }
      truckList.isEmpty match {
        case true => Left("No data found")
        case false => Right(truckList.toSeq)
      }
    }
    catch{
      case NonFatal(exc) =>
        println(s"$func: ${exc.toString}")
        Left(exc.toString)
    }
  }

  def close(): Unit = {
    connection.close()
  }
}
