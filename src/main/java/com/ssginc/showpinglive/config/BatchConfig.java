package com.ssginc.showpinglive.config;

import com.ssginc.showpinglive.service.HlsService;
import com.ssginc.showpinglive.service.SubtitleService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job saveHlsJob(JobRepository jobRepository, Step saveHlsStep) {
        return new JobBuilder("saveHlsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(saveHlsStep)
                .build();
    }

    @Bean
    public Step saveHlsStep(JobRepository jobRepository,
                            PlatformTransactionManager tx,
                            HlsService hlsService) {
        return new StepBuilder("saveHlsStep", jobRepository)
                .tasklet((contrib, ctx) -> {
                    String title = ctx.getStepContext()
                            .getStepExecution()
                            .getJobParameters()
                            .getString("title");
                    hlsService.saveHLS(title);
                    return RepeatStatus.FINISHED;
                }, tx)
                .build();
    }

    @Bean
    public Job createSubtitleJob(JobRepository jobRepository, Step createSubtitleStep) {
        return new JobBuilder("createSubtitleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(createSubtitleStep)
                .build();
    }

    @Bean
    public Step createSubtitleStep(JobRepository jobRepository,
                                   PlatformTransactionManager tx,
                                   SubtitleService subtitleService) {
        return new StepBuilder("createSubtitleStep", jobRepository)
                .tasklet((contrib, ctx) -> {
                    String title = ctx.getStepContext()
                            .getStepExecution()
                            .getJobParameters()
                            .getString("title");
                    subtitleService.createSubtitle(title);
                    return RepeatStatus.FINISHED;
                }, tx)
                .build();
    }

}
