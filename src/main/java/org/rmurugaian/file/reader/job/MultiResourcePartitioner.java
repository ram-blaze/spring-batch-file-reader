package org.rmurugaian.file.reader.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

public class MultiResourcePartitioner implements Partitioner {

  private static final String DEFAULT_KEY_NAME = "fileName";
  private static final String PARTITION_KEY = "partition";

  private final List<ServerAndFilePath> serverAndFilePaths = new ArrayList<>();

  public MultiResourcePartitioner addResource(ServerAndFilePath serverAndFilePath) {
    Assert.notNull(serverAndFilePath, "serverAndFilePath must not be null");
    serverAndFilePaths.add(serverAndFilePath);
    return this;
  }

  /**
   * Assign the filename of each of the injected resources to an {@link ExecutionContext}.
   *
   * @see Partitioner#partition(int)
   */
  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
    Map<String, ExecutionContext> map = new HashMap<>(gridSize);
    int i = 0;
    for (ServerAndFilePath serverAndFilePath : serverAndFilePaths) {
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
