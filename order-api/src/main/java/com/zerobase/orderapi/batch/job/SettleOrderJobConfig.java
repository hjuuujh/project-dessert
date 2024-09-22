package com.zerobase.orderapi.batch.job;

import com.zerobase.orderapi.batch.model.OrderSettleDto;
import com.zerobase.orderapi.domain.order.Settlement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
public class SettleOrderJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JdbcTemplate orderJdbcTemplate;
    private final EntityManagerFactory orderEntityManagerFactory;

    public SettleOrderJobConfig(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            @Qualifier("orderJdbcTemplate") JdbcTemplate orderJdbcTemplate,
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory orderEntityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.orderJdbcTemplate = orderJdbcTemplate;
        this.orderEntityManagerFactory = orderEntityManagerFactory;
    }

    @Bean
    public Job settleOrderJob() {
        log.info("=========== SettleOrderJob Start ===========");
        return jobBuilderFactory.get("settleOrderJob")
//                .preventRestart()
//                .incrementer(new RunIdIncrementer())
                .start(settleOrderStep())
                .build();
    }

    // 매일 04시에 셀러별 정산 테이블 저장
    // 셀러가 날짜 선택해서 정산 요청하면 정산테이블에서 구해서 더해서 리턴 상태는 출금완료로 
    // 환불은 정산테이블에 날짜 검색
    // 없음 -> 바로 환불
    // 출금 전 -> 정산테이블에서 
    @Bean
    @JobScope
    public Step settleOrderStep() {
        log.info("=========== SettleOrderStep Start ===========");
        return stepBuilderFactory.get("settleOrderStep")
                .<OrderSettleDto, Settlement>chunk(100)
                .reader(orderItemReader())
                .processor(settlementItemProcessor())
                .writer(settlementItemWriter())
                .build();
    }


    @StepScope
    @Bean
    public JpaPagingItemReader<OrderSettleDto> orderItemReader() {
        log.info("=========== RepositoryItemReader Start ===========");

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime today = LocalDateTime.now();
        String query = "select new com.zerobase.orderapi.batch.model.OrderSettleDto(" +
                " sellerId, sum(totalPrice) as settlementAmount " +
                ") " +
                " from orders " +
                " where createdAt between :start and :end " +
                " group by sellerId";
        Map<String, Object> params = new HashMap<>();
        params.put("start", yesterday);
        params.put("end", today);
        return new JpaPagingItemReaderBuilder<OrderSettleDto>()
                .name("orderItemReader")
                .entityManagerFactory(orderEntityManagerFactory)
                .pageSize(100)
                .queryString(query)
                .parameterValues(params)
                .build();
    }

    @Bean
    public ItemProcessor<OrderSettleDto, Settlement> settlementItemProcessor() {
        log.info("=========== ItemProcessor Start ===========");

        return orders -> Settlement.builder()
                .sellerId(orders.getSellerId())
                .settlementAmount(orders.getSettlementAmount())
                .build();
    }

    @Bean
    public ItemWriter<Settlement> settlementItemWriter() {
        return settlements -> orderJdbcTemplate.batchUpdate("insert into settlement(seller_id, date, settlement_amount, status, created_at) values (?,?,?,?,?)",
                settlements,
                100,
                (ps, settlement) -> {
                    ps.setObject(1, settlement.getSellerId());
                    ps.setObject(2, LocalDate.now());
                    ps.setObject(3, settlement.getSettlementAmount());
                    ps.setObject(4, "YET");
                    ps.setObject(5, LocalDateTime.now());
                });
    }
}