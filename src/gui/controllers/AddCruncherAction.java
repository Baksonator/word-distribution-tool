package gui.controllers;

import gui.CruncherPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddCruncherAction implements EventHandler<ActionEvent> {

    private CruncherPane cruncherPane;

    public AddCruncherAction(CruncherPane cruncherPane) {
        this.cruncherPane = cruncherPane;
    }

    @Override
    public void handle(ActionEvent event) {
        Stage stage = new Stage();
        stage.setTitle("Enter arity");


        VBox vBox = new VBox();

        TextField tfArity = new TextField("1");

        HBox hBox = new HBox();
        Button okBtn = new Button("OK");
        okBtn.setOnAction(new ConfirmCruncherCreation(cruncherPane, tfArity, stage));
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(event1 -> stage.close());
        hBox.getChildren().add(okBtn);
        hBox.getChildren().add(cancelBtn);

        vBox.getChildren().add(tfArity);
        vBox.getChildren().add(hBox);

        stage.setScene(new Scene(vBox, 300, 300));
        stage.show();
    }
}
