package com.sparta.publicclassdev.global.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AwsS3Util {

  private final AmazonS3 amazonS3;
  private final String bucketName;

  @Autowired
  public AwsS3Util(AmazonS3 amazonS3, String bucketName) {
    this.amazonS3 = amazonS3;
    this.bucketName = bucketName;
  }

  /**
   * 파일을 S3 버킷에 업로드합니다.
   *
   * @param keyName 업로드할 파일의 키 이름
   * @param file    업로드할 파일
   * @throws IOException 파일 처리 중 발생할 수 있는 예외
   */
  public void uploadFile(String keyName, File file) throws IOException {
    try {
      amazonS3.putObject(new PutObjectRequest(bucketName, keyName, file));
    } catch (Exception e) {
      throw new IOException("파일 업로드 실패", e);
    }
  }

  /**
   * S3 버킷에서 파일을 다운로드합니다.
   *
   * @param keyName 다운로드할 파일의 키 이름
   * @return 파일의 InputStream
   * @throws IOException 파일 처리 중 발생할 수 있는 예외
   */
  public InputStream downloadFile(String keyName) throws IOException {
    try {
      S3Object s3Object = amazonS3.getObject(bucketName, keyName);
      return s3Object.getObjectContent();
    } catch (Exception e) {
      throw new IOException("파일 다운로드 실패", e);
    }
  }
}

