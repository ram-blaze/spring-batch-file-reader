package org.rmurugaian.file.reader.job;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rmurugaian.file.reader.persistence.HGuidAndBzCombEntity;
import org.rmurugaian.file.reader.persistence.HGuidAndBzCombRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
@Slf4j
public class LoggingItemWriter implements ItemWriter<HGuidAndBzCombEntity> {

  private final HGuidAndBzCombRepository hGuidAndBzCombRepository;

  @Override
  public void write(@NonNull final Chunk<? extends HGuidAndBzCombEntity> chunk) throws Exception {

    hGuidAndBzCombRepository.saveAll(chunk);
  }
}
