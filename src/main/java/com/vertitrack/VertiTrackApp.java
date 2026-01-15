package com.vertitrack;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // This powers your Reminders/Alerts
public class VertiTrackApp {

    public static void main(String[] args) {
        // This command hands over control to JavaFX (UI)
        Application.launch(JavaFxMain.class, args);
    }
}