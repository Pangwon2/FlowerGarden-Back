package com.parang.flower.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${app.upload-dir}") String uploadDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String path = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    registry.addResourceHandler("/files/**")
            .addResourceLocations("file:" + path);
  }
}

