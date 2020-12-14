package com.javabom.bombatch.sales.job;

import com.javabom.bombatch.config.TestBatchConfig;
import com.javabom.bombatch.sales.model.Sales;
import com.javabom.bombatch.sales.model.SalesRepository;
import com.javabom.bombatch.sales.model.SalesSum;
import com.javabom.bombatch.sales.model.SalesSumRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static com.javabom.bombatch.sales.job.BatchJdbcTestConfiguration.FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {BatchJpaTestConfiguration.class, TestBatchConfig.class})
@SpringBatchTest
public class BatchJpaUnitTestJobConfigurationTest {

    private static final LocalDate orderDate = LocalDate.of(2019, 10, 6);
    @Autowired
    private JpaPagingItemReader<SalesSum> reader;
    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private SalesSumRepository salesSumRepository;

    @AfterEach
    public void tearDown() {
        salesRepository.deleteAllInBatch();
        salesSumRepository.deleteAllInBatch();
    }

    public StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("orderDate", orderDate.format(FORMATTER))
                .toJobParameters();

        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    @DisplayName("기간내 Sales가 집계되어 SalesSum이된다")
    @Test
    void test1() throws Exception {
        //given
        int amount1 = 1000;
        int amount2 = 500;
        int amount3 = 100;

        saveSales(amount1, 1L, 1L);
        saveSales(amount2, 2L, 1L);
        saveSales(amount3, 3L, 1L);

        //when
        reader.open(new ExecutionContext());
        SalesSum salesSum = reader.read();

        //then
        assertThat(salesSum.getAmountSum()).isEqualTo(amount1 + amount2 + amount3);
        assertThat(reader.read()).isNull();
    }

    private Sales saveSales(long amount, Long orderNo, Long ownerNo) {
        return salesRepository.save(new Sales(amount, ownerNo, orderDate, orderNo));
    }
}