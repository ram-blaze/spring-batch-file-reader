/*
 * Copyright 2024, Backblaze, Inc. All rights reserved.
 */
package org.rmurugaian.file.reader.job;

import lombok.NonNull;
import org.rmurugaian.file.reader.persistence.HGuidAndBzCombEntity;
import org.springframework.batch.item.ItemProcessor;

/*
 * @author rmurugaian 2024-09-12
 */
public class HGuidAndBzCombProcessor
    implements ItemProcessor<HGuidAndBzComb, HGuidAndBzCombEntity> {

  private final String serverName;

  public HGuidAndBzCombProcessor(String serverName) {
    this.serverName = serverName;
  }

  @Override
  public HGuidAndBzCombEntity process(@NonNull final HGuidAndBzComb item) {
    final HGuidAndBzCombEntity entity = new HGuidAndBzCombEntity();
    entity.setHguid(item.hGuid());
    entity.setBzcomb(item.bzCombFileName());
    entity.setServer(serverName);
    entity.setServerSize(Long.parseLong(item.serverSize()));
    entity.setClientSize(Long.parseLong(item.clientSize()));
    return entity;
  }
}
