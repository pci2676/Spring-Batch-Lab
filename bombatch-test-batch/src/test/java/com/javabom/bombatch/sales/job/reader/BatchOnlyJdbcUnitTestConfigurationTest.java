package com.javabom.bombatch.sales.job.reader;

import com.javabom.bombatch.sales.job.BatchJdbcTestConfiguration;
import com.javabom.bombatch.sales.model.SalesSum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.time.LocalDate;

import static com.javabom.bombatch.sales.job.BatchJdbcTestConfiguration.FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

@EnableBatchProcessing
@SpringBatchTest
@ContextConfiguration(classes = {
        BatchJdbcTestConfiguration.class,
        BatchOnlyJdbcUnitTestConfigurationTest.TestDataSourceConfiguration.class})
class BatchOnlyJdbcUnitTestConfigurationTest {

    @Autowired
    private JdbcPagingItemReader<SalesSum> reader;

    @Autowired
    private DataSource dataSource;

    private JdbcOperations jdbcTemplate;
    private LocalDate orderDate = LocalDate.of(2019, 10, 6);

    public StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("orderDate", orderDate.format(FORMATTER))
                .toJobParameters();

        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    @BeforeEach
    void setUp() {
        this.reader.setDataSource(this.dataSource);
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @AfterEach
    void tearDown() {
        this.jdbcTemplate.update("delete from sales");
    }


    @DisplayName("기간내 Sales가 집계되어 SalesSum이된다")
    @Test
    void batchOnlyJdbcReaderTestJobReader() throws Exception {
        //given
        long amount1 = 1000;
        long amount2 = 100;
        long amount3 = 10;

        insertSales(1L, amount1, 1L);
        insertSales(2L, amount2, 2L);
        insertSales(3L, amount3, 3L);

        //when
        reader.afterPropertiesSet();
        SalesSum salesSum = reader.read();

        //then
        assertThat(salesSum.getAmountSum()).isEqualTo(amount1 + amount2 + amount3);
        assertThat(reader.read()).isNull();
    }

    private void insertSales(Long id, long amount, long orderNo) {
        jdbcTemplate.update("insert into sales (ID, ORDER_DATE, AMOUNT, ORDER_NO, OWNER_NO) values (?, ?, ?, ?, ?)", id, orderDate, amount, orderNo, 1L);
    }

    @Configuration
    public static class TestDataSourceConfiguration {

        private static final String CREATE_SQL =
                "create table IF NOT EXISTS `sales` (id bigint not null primary key auto_increment, amount bigint not null, order_date date, order_no bigint not null, owner_no bigint not null) engine=InnoDB;";

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseFactory databaseFactory = new EmbeddedDatabaseFactory();
            databaseFactory.setDatabaseType(H2);
            return databaseFactory.getDatabase();
        }

        @Bean
        public DataSourceInitializer initializer(DataSource dataSource) {
            DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
            dataSourceInitializer.setDataSource(dataSource);

            Resource create = new ByteArrayResource(CREATE_SQL.getBytes());
            dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(create));

            return dataSourceInitializer;
        }
    }
}