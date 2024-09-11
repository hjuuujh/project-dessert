package com.zerobase.orderapi.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    // 중간에 실패한경우
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<String> jobNames = jobExplorer.getJobNames();
        for(String jobName: jobNames){
            Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(jobName);

            for(JobExecution jobExecution: runningJobExecutions){
                // status를 fail이나 stopped로 해서 재시작할 수 있도록함
                jobExecution.setStatus(BatchStatus.STOPPED);
                jobExecution.setEndTime(new Date());
                for(StepExecution stepExecution: jobExecution.getStepExecutions()){
                    if(stepExecution.getStatus().isRunning()){
                        stepExecution.setStatus(BatchStatus.STOPPED);
                        stepExecution.setEndTime(new Date());
                        jobRepository.update(jobExecution);
                    }
                }
                jobRepository.update(jobExecution);
            }
        }
    }
}
