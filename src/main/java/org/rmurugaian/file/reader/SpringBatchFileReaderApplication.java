package org.rmurugaian.file.reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SpringBatchFileReaderApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBatchFileReaderApplication.class, args);
  }
}
