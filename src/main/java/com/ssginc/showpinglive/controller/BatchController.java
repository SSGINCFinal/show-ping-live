package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.request.FileRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job saveHlsJob;
    private final Job createSubtitleJob;

    @PostMapping("/hls/create")
    public ResponseEntity<String> saveHLS(@RequestBody FileRequestDto fileRequestDto) throws Exception {
        String title = fileRequestDto.getFileTitle();
        System.out.println(title);
        JobParameters params = new JobParametersBuilder()
                .addString("title", title)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution exec = jobLauncher.run(saveHlsJob, params);
        return ResponseEntity.accepted()
                .body("saveHlsJob 실행 ID=" + exec.getId());
    }

    @PostMapping("/subtitle/create")
    public ResponseEntity<String> createSubtitle(@RequestBody FileRequestDto fileRequestDto) throws Exception {
        String title = fileRequestDto.getFileTitle();
        JobParameters params = new JobParametersBuilder()
                .addString("title", title)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution exec = jobLauncher.run(createSubtitleJob, params);
        return ResponseEntity.accepted()
                .body("createSubtitleJob 실행 ID=" + exec.getId());
    }

}
