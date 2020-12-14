package com.javabom.bombatch.sales.job;

import com.javabom.bombatch.sales.model.SalesSum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchJpaTestConfiguration {
    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");
    public static final String JOB_NAME = "batchJpaUnitTestJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize;

    @Value("${chunkSize:1000}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Bean
    public Job batchJpaUnitTestJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(batchJpaUnitTestJobStep())
                .build();
    }

    @Bean
    public Step batchJpaUnitTestJobStep() {
        return stepBuilderFactory.get("batchJpaUnitTestJobStep")
                .<SalesSum, SalesSum>chunk(chunkSize)
                .reader(batchJpaUnitTestJobReader(null))
                .writer(batchJpaUnitTestJobWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<SalesSum> batchJpaUnitTestJobReader(
            @Value("#{jobParameters[orderDate]}") String orderDate) {

        Map<String, Object> params = new HashMap<>();
        params.put("orderDate", LocalDate.parse(orderDate, FORMATTER));

        String query = String.format("SELECT new %s(s.orderDate, SUM(s.amount)) FROM Sales AS s WHERE s.orderDate =:orderDate GROUP BY s.orderDate",
                SalesSum.class.getName());

        return new JpaPagingItemReaderBuilder<SalesSum>()
                .name("batchJpaUnitTestJobReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(query)
                .parameterValues(params)
                .build();
    }

    @Bean
    public JpaItemWriter<SalesSum> batchJpaUnitTestJobWriter() {
        return new JpaItemWriterBuilder<SalesSum>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}