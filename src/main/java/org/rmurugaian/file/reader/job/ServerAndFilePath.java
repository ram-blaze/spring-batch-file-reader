/*
 * Copyright 2024, Backblaze, Inc. All rights reserved.
 */
package org.rmurugaian.file.reader.job;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;

/*
 * @author rmurugaian 2024-09-13
 */
@Getter
@Setter
public class ServerAndFilePath {
  private String server;
  private ClassPathResource resource;
}
