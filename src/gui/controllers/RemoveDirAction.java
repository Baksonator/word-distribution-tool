package gui.controllers;

import app.App;
import input.FileInput;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class RemoveDirAction implements EventHandler<ActionEvent> {

    private FileInput fileInputComponent;
    private ObservableList<String> dirsList;
    private ListView<String> dirsListView;
    private Button removeDirBtn;
    private String disk;

    public RemoveDirAction(FileInput fileInputComponent, ObservableList<String> dirsList, ListView<String> dirsListView, Button removeDirBtn, String disk) {
        this.fileInputComponent = fileInputComponent;
        this.dirsList = dirsList;
        this.dirsListView = dirsListView;
        this.removeDirBtn = removeDirBtn;
        this.disk = disk;
    }

    @Override
    public void handle(ActionEvent event) {
        String selectedItem = dirsListView.getSelectionModel().getSelectedItem();

        dirsList.remove(selectedItem);
        fileInputComponent.deleteDirectory(App.PATH + disk);
        removeDirBtn.setDisable(true);
    }
}
