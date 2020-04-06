package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerReport {

    public final ReportService reportService;

}
