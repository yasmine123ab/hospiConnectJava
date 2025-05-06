package org.hospiconnect.utils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

/**
 * A modern star-based rating control for JavaFX applications.
 * Allows users to set ratings from 1 to 5 stars with hover effects.
 */
public class RatingBar extends HBox {
    
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final int maxRating;
    private final String starFilled = "M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z";
    private final String starEmpty = "M12,15.39L8.24,17.66L9.23,13.38L5.91,10.5L10.29,10.13L12,6.09L13.71,10.13L18.09,10.5L14.77,13.38L15.76,17.66L12,15.39M22,9.24L14.81,8.63L12,2L9.19,8.63L2,9.24L7.45,13.97L5.82,21L12,17.27L18.18,21L16.54,13.97L22,9.24Z";
    private final Color starFilledColor = Color.web("#FFC107"); // Material gold color
    private final Color starEmptyColor = Color.web("#BDBDBD"); // Material light grey color
    private final Color hoverColor = Color.web("#FFD54F"); // Lighter gold color
    
    /**
     * Creates a new RatingBar with the specified maximum rating.
     *
     * @param maxRating The maximum rating value (number of stars)
     */
    public RatingBar(int maxRating) {
        this.maxRating = maxRating;
        this.setSpacing(5);
        initializeStars();
    }
    
    /**
     * Creates a new RatingBar with a default maximum rating of 5.
     */
    public RatingBar() {
        this(5);
    }
    
    /**
     * Initialize the star shapes and add event handlers.
     */
    private void initializeStars() {
        for (int i = 1; i <= maxRating; i++) {
            final int starValue = i;
            SVGPath starPath = new SVGPath();
            starPath.setContent(starEmpty);
            starPath.setFill(starEmptyColor);
            
            // Make the stars a good size
            starPath.setScaleX(1.5);
            starPath.setScaleY(1.5);
            
            // Style the star
            starPath.getStyleClass().add("rating-star");
            
            // Add hover effects
            starPath.setOnMouseEntered(e -> handleMouseEntered(starValue));
            starPath.setOnMouseExited(e -> handleMouseExited());
            
            // Add click handler to set the rating
            starPath.setOnMouseClicked(e -> setRating(starValue));
            
            getChildren().add(starPath);
        }
        
        // Update the stars when the rating property changes
        rating.addListener((obs, oldVal, newVal) -> updateStars());
    }
    
    /**
     * Handle mouse hover over stars.
     *
     * @param starValue The value (1-5) of the star being hovered
     */
    private void handleMouseEntered(int starValue) {
        for (int i = 0; i < maxRating; i++) {
            SVGPath star = (SVGPath) getChildren().get(i);
            if (i < starValue) {
                star.setContent(starFilled);
                star.setFill(hoverColor);
            } else {
                star.setContent(starEmpty);
                star.setFill(starEmptyColor);
            }
        }
    }
    
    /**
     * Handle mouse exit from stars.
     */
    private void handleMouseExited() {
        updateStars();
    }
    
    /**
     * Update the visual state of stars based on the current rating.
     */
    private void updateStars() {
        int currentRating = getRating();
        
        for (int i = 0; i < maxRating; i++) {
            SVGPath star = (SVGPath) getChildren().get(i);
            if (i < currentRating) {
                star.setContent(starFilled);
                star.setFill(starFilledColor);
            } else {
                star.setContent(starEmpty);
                star.setFill(starEmptyColor);
            }
        }
    }
    
    /**
     * Gets the current rating value.
     *
     * @return The current rating
     */
    public int getRating() {
        return rating.get();
    }
    
    /**
     * Sets the rating value.
     *
     * @param value The new rating value
     */
    public void setRating(int value) {
        if (value >= 0 && value <= maxRating) {
            rating.set(value);
        }
    }
    
    /**
     * Gets the rating property.
     *
     * @return The rating property
     */
    public IntegerProperty ratingProperty() {
        return rating;
    }
}