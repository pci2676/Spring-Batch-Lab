package com.javabom.bombatch.sales.job;

import com.javabom.bombatch.config.TestBatchConfig;
import com.javabom.bombatch.sales.model.Sales;
import com.javabom.bombatch.sales.model.SalesRepository;
import com.javabom.bombatch.sales.model.TaxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

@Import(TestBatchConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TaxingSalesJobConfiguration.class})
@SpringBatchTest
class TaxingSalesJobConfigurationTest {

    //TODO : 왜 못찾는다고 뜨지? 스캔해줬는데..
    //못찾는다고 하는데 찾은 상태
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private TaxRepository taxRepository;

    @DisplayName("processor에서 writer로 list를 넘긴다")
    @Test
    void test1() throws Exception {
        //given
        salesRepository.saveAll(Arrays.asList(
                new Sales(10000L, 1L, LocalDate.of(2020, 12, 11), 1L),
                new Sales(20000L, 2L, LocalDate.of(2020, 12, 11), 1L),
                new Sales(30000L, 3L, LocalDate.of(2020, 12, 11), 1L)));

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(COMPLETED);
        assertThat(taxRepository.findAll()).hasSize(9);
    }
}