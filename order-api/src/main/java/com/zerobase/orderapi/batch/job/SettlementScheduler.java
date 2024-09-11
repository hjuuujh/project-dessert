package com.zerobase.orderapi.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SettlementScheduler {
    private final JobLauncher jobLauncher;
    private final SettleOrderJobConfig settleOrderJobConfig;

    // 매일 새벽 4시 정산 계산
    @Scheduled(cron="0 0 4 * * * ")
//    @Scheduled(cron="0/10 * * * * * ")
    public void runJob(){
        try {
            Map<String, JobParameter> confMap = new HashMap<>();
            confMap.put("time", new JobParameter(System.currentTimeMillis()));
            JobParameters jobParameters = new JobParameters(confMap);
            jobLauncher.run(settleOrderJobConfig.settleOrderJob(), jobParameters);


        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 JobParametersInvalidException | JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}
