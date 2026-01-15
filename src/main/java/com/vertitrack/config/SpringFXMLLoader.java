package com.vertitrack.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * Spring-aware FXML Loader
 * Integrates Spring dependency injection with JavaFX FXML
 */
@Component
public class SpringFXMLLoader {
    
    private final ApplicationContext applicationContext;
    
    public SpringFXMLLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Load FXML file and inject Spring dependencies into controller
     */
    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL fxmlUrl = getClass().getResource(fxmlPath);
        
        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        
        loader.setLocation(fxmlUrl);
        
        // Set controller factory to use Spring context
        loader.setControllerFactory(applicationContext::getBean);
        
        return loader.load();
    }
    
    /**
     * Load FXML and get the loader (useful to access controller)
     */
    public FXMLLoader getLoader(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL fxmlUrl = getClass().getResource(fxmlPath);
        
        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        
        loader.setLocation(fxmlUrl);
        loader.setControllerFactory(applicationContext::getBean);
        
        return loader;
    }
}
