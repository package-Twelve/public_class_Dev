package com.sparta.publicclassdev.global.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

  @PostConstruct
  public void setTimeZone() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
  }
}
