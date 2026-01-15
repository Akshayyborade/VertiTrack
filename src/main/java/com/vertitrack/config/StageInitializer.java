package com.vertitrack.config;

import com.vertitrack.event.StageReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import javafx.stage.Stage;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        // UI logic will go here later
        stage.setTitle("VertiTrack - Lift Management");
        stage.show();
    }
}