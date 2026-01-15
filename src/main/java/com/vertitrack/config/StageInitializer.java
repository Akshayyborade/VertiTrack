package com.vertitrack.config;

import com.vertitrack.event.StageReadyEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    
    private final SpringFXMLLoader fxmlLoader;
    
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            
            // Load the dashboard FXML
            Parent root = fxmlLoader.load("/fxml/dashboard.fxml");
            
            // Create scene
            Scene scene = new Scene(root, 1400, 850);
            
            // Load CSS
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configure stage
            stage.setTitle("VertiTrack - Lift Maintenance & Expense Management System");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            log.info("VertiTrack application started successfully");
            
        } catch (IOException e) {
            log.error("Failed to load dashboard", e);
            e.printStackTrace();
        }
    }
}