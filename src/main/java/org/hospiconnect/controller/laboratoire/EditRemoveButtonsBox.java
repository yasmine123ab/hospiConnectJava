package org.hospiconnect.controller.laboratoire;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class EditRemoveButtonsBox<S> {

    public HBox create(S item, Consumer<S> editCallback, Consumer<S> removeCallback) {
        var editImage = new ImageView("/images/edit.png");
        editImage.setFitHeight(16.0);
        editImage.setFitWidth(16.0);
        editImage.setPreserveRatio(true);

        var removeImage = new ImageView("/images/delete.png");
        removeImage.setFitHeight(16.0);
        removeImage.setFitWidth(16.0);
        removeImage.setPreserveRatio(true);

        Button editButton = new Button();
        editButton.setStyle("-fx-background-color: transparent;");
        editButton.setGraphic(editImage);
        editButton.setOnAction(e -> editCallback.accept(item));

        Button removeButton = new Button();
        removeButton.setStyle("-fx-background-color: transparent;");
        removeButton.setGraphic(removeImage);
        removeButton.setOnAction(e -> removeCallback.accept(item));

        HBox container = new HBox(5, editButton, removeButton);
        container.setAlignment(Pos.CENTER);
        return container;
    }
}
