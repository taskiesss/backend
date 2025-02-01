package taskaya.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import taskaya.backend.services.SignUpService;


@Component
public class ScheduledTask {

    @Autowired
    private SignUpService signUpService;

    @Scheduled(fixedRate = 300000) // Run every 5 minutes (300,000 milliseconds)
    public void executeTask() {
        signUpService.cleanHashMap();
    }


}
