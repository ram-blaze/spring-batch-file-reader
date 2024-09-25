package org.rmurugaian.file.reader;

import org.springframework.boot.SpringApplication;

public class TestSpringBatchFileReaderApplication {

  public static void main(String[] args) {
    SpringApplication.from(SpringBatchFileReaderApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
