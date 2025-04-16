package org.hospiconnect.utils;

import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

/**
 * Utility class providing validation methods for form inputs
 */
public class ValidationUtils {

    /**
     * Visually indicates a validation error on a control
     * @param control The JavaFX control that has an error
     * @param isError True to show error styling, false to clear it
     */
    public static void setErrorStyle(Control control, boolean isError) {
        if (isError) {
            control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            control.setStyle("");
        }
    }
    
    /**
     * Checks if a text input is empty or only contains whitespace
     * @param input The text to validate
     * @return True if the input is null, empty, or only whitespace
     */
    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
    
    /**
     * Validates that a string matches a regular expression pattern
     * @param input The input string to validate
     * @param regex The regular expression to match against
     * @return True if the input matches the pattern
     */
    public static boolean matchesPattern(String input, String regex) {
        return input != null && input.matches(regex);
    }
    
    /**
     * Checks if a string consists only of alphabetic characters, spaces and hyphens
     * @param input The string to validate
     * @return True if the string is a valid name
     */
    public static boolean isValidName(String input) {
        return input != null && input.matches("[a-zA-Z\\s-]+");
    }
    
    /**
     * Checks if a string is within a specified length range
     * @param input The string to validate
     * @param minLength The minimum allowed length
     * @param maxLength The maximum allowed length
     * @return True if the string length is within the specified range
     */
    public static boolean isValidLength(String input, int minLength, int maxLength) {
        return input != null && input.length() >= minLength && input.length() <= maxLength;
    }
}
