package gui.controllers;

import gui.FileInputsPane;
import gui.SingleFileInputPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.util.List;

public class RemoveFileInputAction implements EventHandler<ActionEvent> {

    private FileInputsPane fileInputsPane;
    private SingleFileInputPane singleFileInputPane;

    public RemoveFileInputAction(FileInputsPane fileInputsPane, SingleFileInputPane singleFileInputPane) {
        this.fileInputsPane = fileInputsPane;
        this.singleFileInputPane = singleFileInputPane;
    }

    @Override
    public void handle(ActionEvent event) {
        if (fileInputsPane.getDiskList().getSelectionModel().getSelectedItem().equals(singleFileInputPane.getDisk())) {
            fileInputsPane.getAddFileInputBtn().setDisable(false);
        }

        fileInputsPane.getAddedDisks().remove(singleFileInputPane.getDisk());

        fileInputsPane.getFileInputPanes().remove(singleFileInputPane);
        fileInputsPane.getChildren().remove(singleFileInputPane);
    }
}
