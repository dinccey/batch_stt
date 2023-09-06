package org.vaslim.batch_stt.scheduledjob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vaslim.batch_stt.service.TextFilteringService;

import java.util.Map;
import java.util.Optional;

@Component
//@EnableScheduling
public class FilteringScheduledTask {
    private final TextFilteringService textFilteringService;

    @Value("${filterfile.path:#{null}}")
    private Optional<String> filterMapFilePath;

    public FilteringScheduledTask(TextFilteringService textFilteringService) {
        this.textFilteringService = textFilteringService;
    }

    //@Scheduled(cron = "${JOB_CRON}")
    public void run() {
        if(filterMapFilePath.isPresent()){
            Map<String,String> filterMap = textFilteringService.loadFilterMap(filterMapFilePath.get());
            textFilteringService.processTextFiles(filterMap);
        }
    }

}
