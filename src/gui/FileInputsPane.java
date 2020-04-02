package gui;

import app.App;
import gui.controllers.AddFileInputAction;
import gui.controllers.FileInputComboBoxChange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class FileInputsPane extends VBox {

    private Label inputsHeader;
    private ObservableList<String> disks;
    private ComboBox<String> diskList;
    private Button addFileInputBtn;
    private List<String> addedDisks;
    private List<SingleFileInputPane> fileInputPanes;
    private int paneCounter;
    private App app;

    public FileInputsPane(App app) {
        addedDisks = new ArrayList<>();
        fileInputPanes = new ArrayList<>();
        paneCounter = 0;
        this.app = app;

        initScene();
    }

    private void initScene() {
        inputsHeader = new Label("File inputs");

        String allDisks = App.prop.getProperty("disks");
        String[] disksSeprated = allDisks.split(";");
        disks = FXCollections.observableArrayList(disksSeprated);
        diskList = new ComboBox<>(disks);
        diskList.getSelectionModel().select(0);

        addFileInputBtn = new Button("Add File Input");
        addFileInputBtn.setOnAction(new AddFileInputAction(addedDisks, this));

        // TODO Reorganize this
        diskList.valueProperty().addListener(new FileInputComboBoxChange(addedDisks, addFileInputBtn));

        getChildren().add(inputsHeader);
        getChildren().add(diskList);
        getChildren().add(addFileInputBtn);
    }

    public List<SingleFileInputPane> getFileInputPanes() {
        return fileInputPanes;
    }

    public int getPaneCounter() {
        return paneCounter++;
    }

    public List<String> getAddedDisks() {
        return addedDisks;
    }

    public Button getAddFileInputBtn() {
        return addFileInputBtn;
    }

    public ComboBox<String> getDiskList() {
        return diskList;
    }

    public App getApp() {
        return app;
    }
}
