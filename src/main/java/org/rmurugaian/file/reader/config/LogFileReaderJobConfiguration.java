package org.rmurugaian.file.reader.config;

import java.util.regex.Pattern;
import org.rmurugaian.file.reader.job.*;
import org.rmurugaian.file.reader.persistence.HGuidAndBzCombEntity;
import org.rmurugaian.file.reader.persistence.HGuidAndBzCombRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableConfigurationProperties(FileReaderConfigProperties.class)
class LogFileReaderJobConfiguration {

  private static final String REGEX =
      "Asking HGUID (\\S+) to reupload bzcomb file (\\S+.bzcf) because the size on server was smaller than the size on client. Client version: \\S+, size on server: (\\S+), size from client: (\\S+)";
  private static final Pattern pattern = Pattern.compile(REGEX);

  @Bean
  MultiResourcePartitioner multiResourcePartitioner(
      final FileReaderConfigProperties fileReaderConfigProperties) {

      return new MultiResourcePartitioner(fileReaderConfigProperties);
  }

  @Bean
  @StepScope
  FlatFileItemReader<HGuidAndBzComb> fileReader(
      @Value("#{stepExecutionContext['fileName']}") final Resource resource) {

    final RegexLineTokenizer regexLineTokenizer = new RegexLineTokenizer();
    regexLineTokenizer.setPattern(pattern);

    final FieldSetMapper<HGuidAndBzComb> hGuidAndBzCombFieldSetMapper =
        fieldSet -> fieldSet.getFieldCount() == 4 ?
            new HGuidAndBzComb(
                fieldSet.readRawString(0),
                fieldSet.readRawString(1),
                fieldSet.readRawString(2),
                fieldSet.readRawString(3))
            : null;

    final DefaultLineMapper<HGuidAndBzComb> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(regexLineTokenizer);
    lineMapper.setFieldSetMapper(hGuidAndBzCombFieldSetMapper);

    return new FlatFileItemReaderBuilder<HGuidAndBzComb>()
        .name("logFileReader")
        .lineMapper(lineMapper)
        .resource(resource)
        .build();
  }

  @Bean
  ItemWriter<HGuidAndBzCombEntity> itemWriter(
      final HGuidAndBzCombRepository hGuidAndBzCombRepository) {

    return new LoggingItemWriter(hGuidAndBzCombRepository);
  }

  @Bean
  @StepScope
  ItemProcessor<HGuidAndBzComb, HGuidAndBzCombEntity> itemProcessor(
      @Value("#{stepExecutionContext['serverName']}") final String serverName) {
    return new HGuidAndBzCombProcessor(serverName);
  }

  @Bean
  Step logFileReadStep(
      final ItemReader<HGuidAndBzComb> multiResourceItemReader,
      final ItemProcessor<HGuidAndBzComb, HGuidAndBzCombEntity> itemProcessor,
      final ItemWriter<HGuidAndBzCombEntity> itemWriter,
      final JobRepository jobRepository,
      final PlatformTransactionManager transactionManager) {

    return new StepBuilder("logFileReadStep", jobRepository)
        .<HGuidAndBzComb, HGuidAndBzCombEntity>chunk(100, transactionManager)
        .reader(multiResourceItemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .allowStartIfComplete(true)
        .taskExecutor(new VirtualThreadTaskExecutor())
        .build();
  }

  @Bean
  Step logFileReadPartitionerStep(
      Step logFileReadStep,
      final JobRepository jobRepository,
      final MultiResourcePartitioner multiResourcePartitioner) {

    return new StepBuilder("logFileReadStep.manager", jobRepository)
        .partitioner("logFileReadStep", multiResourcePartitioner)
        .step(logFileReadStep)
        .allowStartIfComplete(true)
        .taskExecutor(new VirtualThreadTaskExecutor())
        .build();
  }

  @Bean
  Job logFileReaderJob(final Step logFileReadPartitionerStep, final JobRepository jobRepository) {

    return new JobBuilder("logFileReaderJob", jobRepository)
        .start(logFileReadPartitionerStep)
        .build();
  }
}
