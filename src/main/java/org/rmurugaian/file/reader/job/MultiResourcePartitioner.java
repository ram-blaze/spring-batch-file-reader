package org.rmurugaian.file.reader.job;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.rmurugaian.file.reader.config.FileReaderConfigProperties;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

@RequiredArgsConstructor
public class MultiResourcePartitioner implements Partitioner {

  private static final String DEFAULT_KEY_NAME = "fileName";
  private static final String PARTITION_KEY = "partition";

  private final FileReaderConfigProperties fileReaderConfigProperties;

  /**
   * Assign the filename of each of the injected resources to an {@link ExecutionContext}.
   *
   * @see Partitioner#partition(int)
   */
  @Override
  public @NonNull Map<String, ExecutionContext> partition(int gridSize) {
    Map<String, ExecutionContext> map = new HashMap<>(gridSize);
    int i = 0;
    for (ServerAndFilePath serverAndFilePath : fileReaderConfigProperties.getServerAndFilePaths()) {
      final ExecutionContext context = new ExecutionContext();
      final ClassPathResource resource = serverAndFilePath.getResource();
      Assert.state(resource.exists(), "Resource does not exist: " + resource);
      try {
        context.putString(DEFAULT_KEY_NAME, resource.getURL().toExternalForm());
        context.putString("serverName", serverAndFilePath.getServer());
      } catch (IOException e) {
        throw new IllegalArgumentException("File could not be located for: " + resource, e);
      }
      map.put(PARTITION_KEY + i, context);
      i++;
    }
    return map;
  }
}
