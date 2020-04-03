package gui.controllers;

import app.App;
import input.FileInput;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class AddDirAction implements EventHandler<ActionEvent> {

    private FileInput fileInputComponent;
    private ObservableList<String> dirsList;
    private String disk;

    public AddDirAction(FileInput fileInputComponent, ObservableList<String> dirsList, String disk) {
        this.fileInputComponent = fileInputComponent;
        this.dirsList = dirsList;
        this.disk = disk;
    }

    @Override
    public void handle(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(App.PATH + disk));
        File selectedFolder = directoryChooser.showDialog(App.primaryStage);

        if (selectedFolder == null) {
            return;
        }

        fileInputComponent.addDirectory(selectedFolder.getAbsolutePath() + "\\");
        dirsList.add(selectedFolder.getAbsolutePath() + "\\");
    }
}
