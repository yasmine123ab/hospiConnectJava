package org.hospiconnect.controller.laboratoire;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class EditRemoveButtons<S> extends TableCell<S, Button> {

    private final Button editButton;
    private final Button removeButton;

    public EditRemoveButtons(Consumer<S> editCallback, Consumer<S> removeCallback) {
        var editImage = new ImageView("/images/edit.png");
        editImage.setFitHeight(16.0);
        editImage.setFitWidth(16.0);
        editImage.preserveRatioProperty().set(true);

        var removeImage = new ImageView("/images/delete.png");
        removeImage.setFitHeight(16.0);
        removeImage.setFitWidth(16.0);
        removeImage.preserveRatioProperty().set(true);
        this.editButton = new Button();
        this.editButton.setStyle("-fx-background-color: transparent;");
        this.editButton.setGraphic(editImage);

        this.removeButton = new Button();
        this.removeButton.setStyle("-fx-background-color: transparent;");
        this.removeButton.setGraphic(removeImage);

        this.editButton.setOnAction(e -> editCallback.accept(getCurrentItem()));
        this.removeButton.setOnAction(e -> removeCallback.accept(getCurrentItem()));
    }

    public S getCurrentItem() {
        return getTableView().getItems().get(getIndex());
    }

    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            var container = new HBox();
            container.setAlignment(Pos.CENTER);
            container.getChildren().addAll(editButton, removeButton);
            setGraphic(container);
        }
    }
}
