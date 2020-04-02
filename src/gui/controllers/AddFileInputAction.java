package gui.controllers;

import gui.FileInputsPane;
import gui.SingleFileInputPane;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.util.List;

public class AddFileInputAction implements EventHandler<ActionEvent> {

    private List<String> addedDisks;
    private FileInputsPane fileInputsPane;

    public AddFileInputAction(List<String> addedDisks, FileInputsPane fileInputsPane) {
        this.addedDisks = addedDisks;
        this.fileInputsPane = fileInputsPane;
    }

    @Override
    public void handle(ActionEvent event) {
        fileInputsPane.getAddFileInputBtn().setDisable(true);

        addedDisks.add(fileInputsPane.getDiskList().getSelectionModel().getSelectedItem());

        SingleFileInputPane newSingleFileInputPane = new SingleFileInputPane(fileInputsPane);
        fileInputsPane.getChildren().add(newSingleFileInputPane);
        fileInputsPane.getFileInputPanes().add(newSingleFileInputPane);
    }
}
