/*
 * Copyright 2024, Backblaze, Inc. All rights reserved.
 */
package org.rmurugaian.file.reader.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.rmurugaian.file.reader.job.ServerAndFilePath;
import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * @author rmurugaian 2024-09-13
 */
@ConfigurationProperties(prefix = "batch")
@Getter
@Setter
public class FileReaderConfigProperties {
  private final List<ServerAndFilePath> serverAndFilePaths = new ArrayList<>();
}
