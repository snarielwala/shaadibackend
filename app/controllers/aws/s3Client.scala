package controllers.aws

import java.io.File
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}
import com.typesafe.config.ConfigFactory

/**
 * Created by pdesai on 10/10/15.
 */
object s3 {
  var amazonS3Client: AmazonS3Client = null
  var s3Bucket: String = null

  // We are not catching exceptions here. If init fails, we want to know about it
  def init(): Unit = {
    var conf = ConfigFactory.load
    val yourAWSCredentials = new BasicAWSCredentials(conf.getString("aws.accesskey"), conf.getString("aws.secretKey"))
    s3Bucket = conf.getString("s3bucket.pictures")
    amazonS3Client = new AmazonS3Client(yourAWSCredentials)
  }

  def uploadFile(fileName: String, fileToUpload: File): Either[String, String] = {
    uploadToS3(s3Bucket, fileName, fileToUpload)
  }

  private def uploadToS3(bucketName: String, fileName: String, fileToUpload: File): Either[String, String] = {
    try {
      val url = amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, fileToUpload).withCannedAcl(CannedAccessControlList.PublicRead))
      Right(amazonS3Client.getResourceUrl(bucketName, fileName))
    }
    catch {
      case e: Exception =>
        Left(s"Could not upload file $fileName to S3: ${e.toString}")
    }
  }
}
