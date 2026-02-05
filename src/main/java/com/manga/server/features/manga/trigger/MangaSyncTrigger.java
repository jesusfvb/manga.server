package com.manga.server.features.manga.trigger;

import com.manga.server.features.manga.user_cases.SyncMangasUseCase;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MangaSyncTrigger {

    private final SyncMangasUseCase useCase;

    public MangaSyncTrigger(SyncMangasUseCase useCase) {
        this.useCase = useCase;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void run() {
        useCase.execute();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        useCase.execute();
    }

}
