package org.hospiconnect.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to validate FXML files and identify issues
 */
public class FXMLValidator {

    /**
     * Validates an FXML file and returns any errors found
     * 
     * @param fxmlPath The path to the FXML file
     * @return A list of error messages, empty if no errors
     */
    public static List<String> validateFXML(String fxmlPath) {
        List<String> errors = new ArrayList<>();
        
        // Check if resource exists
        URL resourceUrl = FXMLValidator.class.getClassLoader().getResource(fxmlPath);
        if (resourceUrl == null) {
            errors.add("Resource not found: " + fxmlPath);
            return errors;
        }
        
        // Try to load the FXML
        try {
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            if (root == null) {
                errors.add("FXML loaded but root element is null");
            } else {
                System.out.println("FXML loaded successfully: " + fxmlPath);
            }
        } catch (IOException e) {
            errors.add("IO Error: " + e.getMessage());
            if (e.getCause() != null) {
                errors.add("Caused by: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            errors.add("Error: " + e.getMessage());
            if (e.getCause() != null) {
                errors.add("Caused by: " + e.getCause().getMessage());
            }
        }
        
        return errors;
    }
    
    /**
     * Main method to run validation from command line
     */
    public static void main(String[] args) {
        // Validate the problematic FXML file
        List<String> errors = validateFXML("reservation_form_view.fxml");
        
        if (errors.isEmpty()) {
            System.out.println("No errors found in FXML file.");
        } else {
            System.out.println("Errors found in FXML file:");
            for (String error : errors) {
                System.out.println("- " + error);
            }
        }
    }
}
