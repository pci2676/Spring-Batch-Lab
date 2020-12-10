package com.javabom.bombatch.sales.job;

import com.javabom.bombatch.sales.job.processor.ItemListProcessor;
import com.javabom.bombatch.sales.job.writer.JpaItemListWriter;
import com.javabom.bombatch.sales.model.Sales;
import com.javabom.bombatch.sales.model.Tax;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Program arguments --job.name=taxingSalesJob requestDate=2020-12-11
 */
@Configuration
@RequiredArgsConstructor
public class TaxingSalesJobConfiguration {
    public static final String JOB_NAME = "taxingSalesJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    private Step step() {
        return stepBuilderFactory.get("taxingSalesStep")
                .<Sales, List<Tax>>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .writer(writerList())
                .build();
    }

    private ItemProcessor<Sales, List<Tax>> processor() {
        return new ItemListProcessor();
    }

    private JpaPagingItemReader<Sales> reader() {
        JpaPagingItemReader<Sales> reader = new JpaPagingItemReader<>();
        reader.setQueryString("select s from Sales s");
        reader.setEntityManagerFactory(entityManagerFactory);
        return reader;
    }

    private JpaItemWriter<List<Tax>> writer() {
        JpaItemWriter<List<Tax>> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    private JpaItemListWriter<Tax> writerList() {
        JpaItemWriter<Tax> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);

        return new JpaItemListWriter<>(writer);
    }
}
